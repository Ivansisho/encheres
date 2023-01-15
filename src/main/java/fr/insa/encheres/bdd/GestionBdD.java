/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fr.insa.encheres.bdd;

import fr.insa.encheres.model.Objet;
import fr.insa.encheres.model.Utilisateur;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Optional;

/**
 *
 * @author ivane
 */
public class GestionBdD {

    public static Connection connectGeneralPostGres(String host,
            int port, String database,
            String user, String pass)
            throws ClassNotFoundException, SQLException {
        Class.forName("org.postgresql.Driver");
        Connection con = DriverManager.getConnection(
                "jdbc:postgresql://" + host + ":" + port
                + "/" + database,
                user, pass);
        con.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
        return con;
    }

    public static Connection defautConnect()
            throws ClassNotFoundException, SQLException {
        return connectGeneralPostGres("localhost", 5432, "postgres", "postgres", "pass");
    }

    public static void createSchema(Connection con)
            throws SQLException {
        // je veux que le schema soit entierement créé ou pas du tout
        // je vais donc gérer explicitement une transaction
        con.setAutoCommit(false);
        try (Statement st = con.createStatement()) {
            // creation des tables
            st.executeUpdate(
                    """
                    create table utilisateur (
                        id integer not null primary key
                        generated always as identity,
                        nom varchar(50) not null,
                        prenom varchar(50) not null,
                        pass varchar(50) not null,
                        email varchar(100) not null unique,
                        codepostal varchar(50) not null
                    )
                    """);
            st.executeUpdate(
                    """
                    create table objet (
                        id integer not null primary key
                        generated always as identity,
                        titre varchar(200) not null,
                        description text not null,
                        debut timestamp not null,
                        fin timestamp not null,
                        prixbase integer not null,
                        categorie integer not null,
                        proposepar integer not null
                    )
                    """);
            st.executeUpdate(
                    """
                    create table enchere (
                        id integer not null primary key
                        generated always as identity,
                        quand timestamp not null,
                        montant integer not null,
                        de integer not null,
                        sur integer not null
                    )
                    """);
            st.executeUpdate(
                    """
                    create table categorie (
                       id integer not null primary key
                       generated always as identity,
                       nom varchar(50) not null
                    )
                    """);

            // je defini les liens entre les clés externes et les clés primaires
            // correspondantes
            st.executeUpdate(
                    """
                    alter table enchere
                        add constraint fk_enchere_de
                        foreign key (de) references utilisateur(id)
                    
                    """);
            st.executeUpdate(
                    """
                    alter table enchere
                        add constraint fk_enchere_sur
                        foreign key (sur) references objet(id)
                    
                    """);
            st.executeUpdate(
                    """
                    alter table objet
                        add constraint fk_objet_categorie
                        foreign key (categorie) references categorie(id)
                    """);
            st.executeUpdate(
                    """
                    alter table objet
                        add constraint fk_objet_proposepar
                        foreign key (proposepar) references utilisateur(id)
                    """);
            // si j'arrive jusqu'ici, c'est que tout s'est bien passé
            // je confirme (commit) la transaction
            con.commit();
            // je retourne dans le mode par défaut de gestion des transaction :
            // chaque ordre au SGBD sera considéré comme une transaction indépendante
            con.setAutoCommit(true);
        } catch (SQLException ex) {
            // quelque chose s'est mal passé
            // j'annule la transaction
            con.rollback();
            // puis je renvoie l'exeption pour qu'elle puisse éventuellement
            // être gérée (message à l'utilisateur...)
            throw ex;
        } finally {
            // je reviens à la gestion par défaut : une transaction pour
            // chaque ordre SQL
            con.setAutoCommit(true);
        }
    }

    public static void deleteSchema(Connection con) throws SQLException {
        try (Statement st = con.createStatement()) {
            // pour être sûr de pouvoir supprimer, il faut d'abord supprimer les liens
            // puis les tables
            // suppression des liens
            try {
                st.executeUpdate(
                        """
                    alter table enchere
                        drop constraint fk_enchere_de
                             """);
                System.out.println("constraint fk_enchere_de dropped");
            } catch (SQLException ex) {
                // nothing to do : maybe the constraint was not created
            }
            try {
                st.executeUpdate(
                        """
                    alter table enchere
                        drop constraint fk_enchere_sur
                    """);
                System.out.println("constraint fk_enchere_sur dropped");
            } catch (SQLException ex) {
                // nothing to do : maybe the constraint was not created
            }
            try {
                st.executeUpdate(
                        """
                    alter table objet
                        drop constraint fk_objet_categorie
                    """);
                System.out.println("constraint fk_objet_categorie dropped");
            } catch (SQLException ex) {
                // nothing to do : maybe the constraint was not created
            }
            try {
                st.executeUpdate(
                        """
                    alter table objet
                        drop constraint fk_objet_proposepar
                    """);
                System.out.println("constraint fk_objet_proposepar dropped");
            } catch (SQLException ex) {
                // nothing to do : maybe the constraint was not created
            }

            //une fois les contraintes supprimées, on peut supprimer les tables :
            try {
                st.executeUpdate(
                        """
                    drop table utilisateur
                    """);
                System.out.println("table utilisateur dropped");
            } catch (SQLException ex) {
                // nothing to do : maybe the table was not created
            }
            try {
                st.executeUpdate(
                        """
                    drop table objet
                    """);
                System.out.println("table objet dropped");
            } catch (SQLException ex) {
                // nothing to do : maybe the table was not created
            }
            try {
                st.executeUpdate(
                        """
                    drop table enchere
                    """);
                System.out.println("table enchere dropped");
            } catch (SQLException ex) {
                // nothing to do : maybe the table was not created
            }
            try {
                st.executeUpdate(
                        """
                    drop table categorie
                    """);
                System.out.println("table categorie dropped");
            } catch (SQLException ex) {
                // nothing to do : maybe the table was not created
            }
        }

    }

    public static void createUtilisateur(Connection con, String nom, String prenom, String pass, String email, String codepostal)
            throws SQLException {
        con.setAutoCommit(false);
        try (PreparedStatement pst = con.prepareStatement(
                """
                    insert into utilisateur (nom, prenom, pass, email, codepostal)
                    values (?, ?, ?, ?, ?)
                    """)) {
            pst.setString(1, nom);
            pst.setString(2, prenom);
            pst.setString(3, pass);
            pst.setString(4, email);
            pst.setString(5, codepostal);
            pst.executeUpdate();
            con.commit();
            con.setAutoCommit(true);
        } catch (SQLException ex) {
            con.rollback();
            throw ex;
        } finally {
            con.setAutoCommit(true);
        }
    }

    public static void AfficherUtilisateurs(Connection con)
            throws SQLException {
        con.setAutoCommit(false);
        try (Statement st = con.createStatement()) {
            ResultSet result = st.executeQuery(
                    """
                    select * from utilisateur
                    """
            );

            while (result.next()) {
                int id = result.getInt("id");
                String nom = result.getString("nom");
                String prenom = result.getString("prenom");
                String codepostal = result.getString("codepostal");
                String email = result.getString("email");
                String pass = result.getString("pass");
                System.out.println(id + " : " + nom + " " + prenom + " " + codepostal + " " + email + " " + pass);

            }
        } catch (SQLException ex) {
            // quelque chose s'est mal passé
            // j'annule la transaction
            con.rollback();
            // puis je renvoie l'exeption pour qu'elle puisse éventuellement
            // être gérée (message à l'utilisateur...)
            throw ex;
        } finally {
            // je reviens à la gestion par défaut : une transaction pour
            // chaque ordre SQL
            con.setAutoCommit(true);
        }
    }

    public static void LireUtilisateur(Connection con)
            throws SQLException {
        System.out.println("Nom de l'utilisateur :");
        String nom = Lire.S();
        System.out.println("Prenom de l'utilisateur:");
        String prenom = Lire.S();
        System.out.println("Mot de passe de l'utilisateur :");
        String pass = Lire.S();
        System.out.println("Email de l'utilisateur :");
        String email = Lire.S();
        System.out.println("Code postal de l'utilisateur :");
        String codepostal = Lire.S();
        createUtilisateur(con, nom, prenom, pass, email, codepostal);

    }

    public static class NomExisteDejaException extends Exception {
    }

    public static int IdUtilisateurExiste(Connection con, String email)
            throws SQLException {
        ResultSet result;
        int id = -1;
        con.setAutoCommit(false);
        try (Statement st = con.createStatement()) {
            result = st.executeQuery(
                    "select id from utilisateur where email like'" + email + "'"
            );
            while (result.next()) {
                id = result.getInt("id");
            }
        } catch (SQLException ex) {
            // quelque chose s'est mal passé
            // j'annule la transaction
            con.rollback();
            // puis je renvoie l'exeption pour qu'elle puisse éventuellement
            // être gérée (message à l'utilisateur...)
            throw ex;
        } finally {
            // je reviens à la gestion par défaut : une transaction pour
            // chaque ordre SQL
            con.setAutoCommit(true);
        }
        return id;
    }

    public static String PassUtilisateurExiste(Connection con, String email)
            throws SQLException {
        ResultSet result;
        String pass = null;
        con.setAutoCommit(false);
        try (Statement st = con.createStatement()) {
            result = st.executeQuery(
                    "select pass from utilisateur where email like'" + email + "'"
            );
            //sauvegarde les résultats
            while (result.next()) {
                pass = result.getString("pass");
            }
        } catch (SQLException ex) {
            // quelque chose s'est mal passé
            // j'annule la transaction
            con.rollback();
            // puis je renvoie l'exeption pour qu'elle puisse éventuellement
            // être gérée (message à l'utilisateur...)
            throw ex;
        } finally {
            // je reviens à la gestion par défaut : une transaction pour
            // chaque ordre SQL
            con.setAutoCommit(true);
        }
        return pass;
    }

    public static String EmailUtilisateurExiste(Connection con, int id)
            throws SQLException {
        ResultSet result;
        String email = null;
        con.setAutoCommit(false);
        try (Statement st = con.createStatement()) {
            result = st.executeQuery(
                    "select email from utilisateur where id = " + id + ""
            );
            //sauvegarde les résultats
            while (result.next()) {
                email = result.getString("email");
            }
        } catch (SQLException ex) {
            // quelque chose s'est mal passé
            // j'annule la transaction
            con.rollback();
            // puis je renvoie l'exeption pour qu'elle puisse éventuellement
            // être gérée (message à l'utilisateur...)
            throw ex;
        } finally {
            // je reviens à la gestion par défaut : une transaction pour
            // chaque ordre SQL
            con.setAutoCommit(true);
        }
        return email;
    }

    public static String CodePostalUtilisateurExiste(Connection con, String email)
            throws SQLException {
        ResultSet result;
        String CodePostal = null;
        con.setAutoCommit(false);
        try (Statement st = con.createStatement()) {
            result = st.executeQuery(
                    "select codepostal from utilisateur where email like'" + email + "'"
            );
            //sauvegarde les résultats
            while (result.next()) {
                CodePostal = result.getString("codepostal");
            }
        } catch (SQLException ex) {
            // quelque chose s'est mal passé
            // j'annule la transaction
            con.rollback();
            // puis je renvoie l'exeption pour qu'elle puisse éventuellement
            // être gérée (message à l'utilisateur...)
            throw ex;
        } finally {
            // je reviens à la gestion par défaut : une transaction pour
            // chaque ordre SQL
            con.setAutoCommit(true);
        }
        return CodePostal;
    }

    public static String UtilisateurExiste(Connection con, int id)
            throws SQLException {
        ResultSet result;
        String utilisateur = null;
        con.setAutoCommit(false);
        try (Statement st = con.createStatement()) {
            result = st.executeQuery(
                    """
                    select nom,prenom from utilisateur where id =""" + id + """                                          
                    """
            );
            //sauvegarde les résultats
            while (result.next()) {
                utilisateur = result.getString("nom") + " " + result.getString("prenom");

            }
        } catch (SQLException ex) {
            // quelque chose s'est mal passé
            // j'annule la transaction
            con.rollback();
            // puis je renvoie l'exeption pour qu'elle puisse éventuellement
            // être gérée (message à l'utilisateur...)
            throw ex;
        } finally {
            // je reviens à la gestion par défaut : une transaction pour
            // chaque ordre SQL
            con.setAutoCommit(true);
        }
        return utilisateur;
    }

    public static ArrayList TousLesUtilisateurs(Connection con)
            throws SQLException {
        ResultSet result;
        ArrayList<String> listeUtilisateurs = new ArrayList<String>();
        con.setAutoCommit(false);
        try (Statement st = con.createStatement()) {
            result = st.executeQuery(
                    """
                    select email from utilisateur
                    """
            );
            //sauvegarde les résultats
            while (result.next()) {
                listeUtilisateurs.add(result.getString("email"));
            }
        } catch (SQLException ex) {
            // quelque chose s'est mal passé
            // j'annule la transaction
            con.rollback();
            // puis je renvoie l'exeption pour qu'elle puisse éventuellement
            // être gérée (message à l'utilisateur...)
            throw ex;
        } finally {
            // je reviens à la gestion par défaut : une transaction pour
            // chaque ordre SQL
            con.setAutoCommit(true);
        }
        return listeUtilisateurs;
    }

    public static void createEnchere(Connection con, Timestamp quand, int montant, int de, int sur)
            throws SQLException {
        con.setAutoCommit(false);
        try (PreparedStatement pst = con.prepareStatement(
                """
                    insert into enchere(quand, montant, de, sur)
                    values (?, ?, ?, ?)
                    """)) {
            pst.setTimestamp(1, quand);
            pst.setInt(2, montant);
            pst.setInt(3, de);
            pst.setInt(4, sur);
            pst.executeUpdate();
            con.commit();
            con.setAutoCommit(true);
        } catch (SQLException ex) {
            con.rollback();
            throw ex;
        } finally {
            con.setAutoCommit(true);
        }
    }

    public static void AfficherEncheres(Connection con)
            throws SQLException {
        con.setAutoCommit(false);
        try (Statement st = con.createStatement()) {
            ResultSet result = st.executeQuery(
                    """
                    select * from enchere
                    """
            );
            System.out.println("Liste des encheres :");
            while (result.next()) {
                int id = result.getInt("id");
                String quand = result.getString("quand");
                int montant = result.getInt("montant");
                int de = result.getInt("de");
                int sur = result.getInt("sur");
                System.out.println(id + " : " + quand + " " + montant + "€ " + de + " " + sur);
            }
        } catch (SQLException ex) {
            // quelque chose s'est mal passé
            // j'annule la transaction
            con.rollback();
            // puis je renvoie l'exeption pour qu'elle puisse éventuellement
            // être gérée (message à l'utilisateur...)
            throw ex;
        } finally {
            // je reviens à la gestion par défaut : une transaction pour
            // chaque ordre SQL
            con.setAutoCommit(true);
        }
    }

    public static int PrixMaxEnchere(Connection con, int IdObjet)
            throws SQLException {
        ResultSet result;
        int PrixMax = 0;
        con.setAutoCommit(false);
        try (Statement st = con.createStatement()) {
            result = st.executeQuery(
                    "select MAX(montant) from enchere where sur=" + IdObjet + ""
            );
            //sauvegarde les résultats
            while (result.next()) {
                PrixMax = result.getInt(1);
            }
        } catch (SQLException ex) {
            // quelque chose s'est mal passé
            // j'annule la transaction
            con.rollback();
            // puis je renvoie l'exeption pour qu'elle puisse éventuellement
            // être gérée (message à l'utilisateur...)
            throw ex;
        } finally {
            // je reviens à la gestion par défaut : une transaction pour
            // chaque ordre SQL
            con.setAutoCommit(true);
        }
        return PrixMax;
    }

    public static void AfficherCategorie(Connection con)
            throws SQLException {
        con.setAutoCommit(false);
        try (Statement st = con.createStatement()) {
            ResultSet result = st.executeQuery(
                    """
                    select * from categorie
                    """
            );
            System.out.println("Liste des categories :");
            while (result.next()) {
                int id = result.getInt("id");
                String nom = result.getString("nom");
                System.out.println(id + " : " + nom);
            }
        } catch (SQLException ex) {
            // quelque chose s'est mal passé
            // j'annule la transaction
            con.rollback();
            // puis je renvoie l'exeption pour qu'elle puisse éventuellement
            // être gérée (message à l'utilisateur...)
            throw ex;
        } finally {
            // je reviens à la gestion par défaut : une transaction pour
            // chaque ordre SQL
            con.setAutoCommit(true);
        }
    }

    public static ArrayList CategoriesExiste(Connection con)
            throws SQLException {
        ResultSet result;
        ArrayList<String> ListeCategories = new ArrayList<>();
        con.setAutoCommit(false);
        try (Statement st = con.createStatement()) {
            result = st.executeQuery(
                    """
                    select * from categorie
                    """
            );
            //sauvegarde les résultats
            while (result.next()) {
                ListeCategories.add(result.getString("nom"));
            }
        } catch (SQLException ex) {
            // quelque chose s'est mal passé
            // j'annule la transaction
            con.rollback();
            // puis je renvoie l'exeption pour qu'elle puisse éventuellement
            // être gérée (message à l'utilisateur...)
            throw ex;
        } finally {
            // je reviens à la gestion par défaut : une transaction pour
            // chaque ordre SQL
            con.setAutoCommit(true);
        }
        return ListeCategories;
    }

    public static int IdCategorieExiste(Connection con, String nom)
            throws SQLException {
        ResultSet result;
        int id = -1;
        con.setAutoCommit(false);
        try (Statement st = con.createStatement()) {
            result = st.executeQuery(
                    "select id from categorie where nom like'" + nom + "'"
            );
            //sauvegarde les résultats
            while (result.next()) {
                id = result.getInt("id");
            }
        } catch (SQLException ex) {
            // quelque chose s'est mal passé
            // j'annule la transaction
            con.rollback();
            // puis je renvoie l'exeption pour qu'elle puisse éventuellement
            // être gérée (message à l'utilisateur...)
            throw ex;
        } finally {
            // je reviens à la gestion par défaut : une transaction pour
            // chaque ordre SQL
            con.setAutoCommit(true);
        }
        return id;
    }

    public static void createCategorie(Connection con, String nom)
            throws SQLException {
        con.setAutoCommit(false);
        try (PreparedStatement pst = con.prepareStatement(
                """
                    insert into categorie (nom)
                    values (?)
                    """)) {
            pst.setString(1, nom);
            pst.executeUpdate();
            con.commit();
            con.setAutoCommit(true);
        } catch (SQLException ex) {
            con.rollback();
            throw ex;
        } finally {
            con.setAutoCommit(true);
        }
    }

    public static ArrayList ObjetsExiste(Connection con)
            throws SQLException, IOException, ClassNotFoundException {
        ResultSet result;
        ArrayList<Objet> listeObjets = new ArrayList<>();
        con.setAutoCommit(false);
        try (Statement st = con.createStatement()) {
            result = st.executeQuery(
                    """
                    select * from objet
                    """
            );
            //sauvegarde les résultats
            while (result.next()) {
                int id = result.getInt("id");
                String titre = result.getString("titre");
                String description = result.getString("description");
                Timestamp debut = result.getTimestamp("debut");
                Timestamp fin = result.getTimestamp("fin");
                int categorie = result.getInt("categorie");
                int prixbase = result.getInt("prixbase");
                int proposepar = result.getInt("proposepar");
                listeObjets.add(new Objet(id, titre, description, debut, fin, categorie, prixbase, proposepar));
            }
        } catch (SQLException ex) {
            // quelque chose s'est mal passé
            // j'annule la transaction
            con.rollback();
            // puis je renvoie l'exeption pour qu'elle puisse éventuellement
            // être gérée (message à l'utilisateur...)
            throw ex;
        } finally {
            // je reviens à la gestion par défaut : une transaction pour
            // chaque ordre SQL
            con.setAutoCommit(true);
        }
        return listeObjets;
    }

    public static String FinObjetExiste(Connection con, int idObjet)
            throws SQLException {
        ResultSet result;
        String fin = null;
        con.setAutoCommit(false);
        try (Statement st = con.createStatement()) {
            result = st.executeQuery(
                    """
                    select fin from objet where id = """ + idObjet + """
                                                             
                    """
            );
            //sauvegarde les résultats
            while (result.next()) {
                fin = result.getString("fin");
            }
        } catch (SQLException ex) {
            // quelque chose s'est mal passé
            // j'annule la transaction
            con.rollback();
            // puis je renvoie l'exeption pour qu'elle puisse éventuellement
            // être gérée (message à l'utilisateur...)
            throw ex;
        } finally {
            // je reviens à la gestion par défaut : une transaction pour
            // chaque ordre SQL
            con.setAutoCommit(true);
        }
        return fin;
    }
    public static String motcle;

    public static ArrayList ChercherObjetsMotCle(Connection con)
            throws SQLException, IOException, ClassNotFoundException {
        ResultSet result;
        ArrayList<Objet> listeObjets = new ArrayList<>();
        motcle = Lire.S();
        con.setAutoCommit(false);
        try (Statement st = con.createStatement()) {
            result = st.executeQuery(
                    """
                    select * from objet where titre like '"+motcle+"' or description like '%"+motcle+"%'
                    """
            );
            System.out.println("Voici la liste des objets avec ce(s) mot(s) clé(s) :");
            while (result.next()) {
                int id = result.getInt("id");
                String titre = result.getString("titre");
                String description = result.getString("description");
                Timestamp debut = result.getTimestamp("debut");
                Timestamp fin = result.getTimestamp("fin");
                int categorie = result.getInt("categorie");
                int prixbase = result.getInt("prixbase");
                int proposepar = result.getInt("proposepar");
                listeObjets.add(new Objet(id, titre, description, debut, fin, categorie, prixbase, proposepar));
                System.out.println(listeObjets);
            }
        } catch (SQLException ex) {
            // quelque chose s'est mal passé
            // j'annule la transaction
            con.rollback();
            // puis je renvoie l'exeption pour qu'elle puisse éventuellement
            // être gérée (message à l'utilisateur...)
            throw ex;
        } finally {
            // je reviens à la gestion par défaut : une transaction pour
            // chaque ordre SQL
            con.setAutoCommit(true);
        }
        return listeObjets;
    }

    public static void LireCategorie(Connection con)
            throws SQLException {
        System.out.println("nom categorie :");
        String nom = Lire.S();
        createCategorie(con, nom);
    }

    public static void AfficherObjets(Connection con)
            throws SQLException {
        con.setAutoCommit(false);
        try (Statement st = con.createStatement()) {
            ResultSet result = st.executeQuery(
                    """
                    select * from objet
                    """
            );
            System.out.println("Liste des objets :");
            while (result.next()) {
                int id = result.getInt("id");
                String titre = result.getString("titre");
                String description = result.getString("description");
                String debut = result.getString("debut");
                String fin = result.getString("fin");
                String prixbase = result.getString("prixbase");
                int categorie = result.getInt("categorie");
                int proposepar = result.getInt("proposepar");
                System.out.println(id + " : " + titre + " " + description + " " + debut + " " + fin + " " + prixbase + "€ " + categorie + " " + proposepar);
            }
        } catch (SQLException ex) {
            // quelque chose s'est mal passé
            // j'annule la transaction
            con.rollback();
            // puis je renvoie l'exeption pour qu'elle puisse éventuellement
            // être gérée (message à l'utilisateur...)
            throw ex;
        } finally {
            // je reviens à la gestion par défaut : une transaction pour
            // chaque ordre SQL
            con.setAutoCommit(true);
        }
    }

    public static String TitreObjetExiste(Connection con, int id)
            throws SQLException {
        ResultSet result;
        String titre = null;
        con.setAutoCommit(false);
        try (Statement st = con.createStatement()) {
            result = st.executeQuery(
                    "select titre from objet where id = " + id + " "
            );
            //sauvegarde les résultats
            while (result.next()) {
                titre = result.getString("titre");
            }
        } catch (SQLException ex) {
            // quelque chose s'est mal passé
            // j'annule la transaction
            con.rollback();
            // puis je renvoie l'exeption pour qu'elle puisse éventuellement
            // être gérée (message à l'utilisateur...)
            throw ex;
        } finally {
            // je reviens à la gestion par défaut : une transaction pour
            // chaque ordre SQL
            con.setAutoCommit(true);
        }
        return titre;
    }

    public static void createObjet(Connection con, String titre, String description, Timestamp debut, Timestamp fin, int prixbase, int categorie, int proposepar)
            throws SQLException, IOException {
        con.setAutoCommit(false);
        try (PreparedStatement pst = con.prepareStatement(
                """
                    insert into objet (titre, description, debut, fin, prixbase, categorie, proposepar)
                    values (?, ?, ?, ?, ?, ?, ?)
                    """)) {
            pst.setString(1, titre);
            pst.setString(2, description);
            pst.setTimestamp(3, debut);
            pst.setTimestamp(4, fin);
            pst.setInt(5, prixbase);
            pst.setInt(6, categorie);
            pst.setInt(7, proposepar);
            pst.executeUpdate();
            con.commit();
            con.setAutoCommit(true);
        } catch (SQLException ex) {
            con.rollback();
            throw ex;
        } finally {
            con.setAutoCommit(true);
        }
    }

    public static void LireObjet(Connection con)
            throws SQLException, IOException {
        System.out.println("Titre de l'objet :");
        String titre = Lire.S();
        System.out.println("Description de l'objet :");
        String description = Lire.S();
        System.out.println("Debut de la vente Année:");
        int anneed = Lire.i();
        System.out.println("Debut de la vente Mois:");
        int moisd = Lire.i();
        System.out.println("Debut de la vente Jour:");
        int jourd = Lire.i();
        System.out.println("Debut de la vente Heure:");
        int heured = Lire.i();
        Timestamp debutobjet = new Timestamp(anneed-1900, moisd-1, jourd, heured, 0, 0, 0);
        System.out.println("Fin de la vente Année:");
        int anneef = Lire.i();
        System.out.println("Fin de la vente Mois:");
        int moisf = Lire.i();
        System.out.println("Fin de la vente Jour:");
        int jourf = Lire.i();
        System.out.println("Fin de la vente Heure:");
        int heuref = Lire.i();
        Timestamp finobjet = new Timestamp(anneef-1900, moisf-1, jourf, heuref, 0, 0, 0);
        System.out.println("Prix de base de l'objet :");
        int prixbase = Lire.i();
        System.out.println("Categorie de l'objet :");
        int proposepar = Lire.i();
        System.out.println("Objet proposé par :");
        int categorie = Lire.i();
        createObjet(con, titre, description, debutobjet, finobjet, prixbase, categorie, proposepar);
    }
    
    public static void LireEnchere(Connection con)
            throws SQLException, IOException {
        System.out.println("Date de l'enchère Année:");
        int anneed = Lire.i();
        System.out.println("Date de l'enchère Mois:");
        int moisd = Lire.i();
        System.out.println("Date de l'enchère Jour:");
        int jourd = Lire.i();
        System.out.println("Date de l'enchère Heure:");
        int heured = Lire.i();
        Timestamp debutobjet = new Timestamp(anneed, moisd, jourd, heured, 0, 0, 0);
        System.out.println("Prix de l'enchère :");
        int montant = Lire.i();
        System.out.println("Votre id d'utilisateur :");
        int de = Lire.i();
        System.out.println("Objet désiré :");
        int sur = Lire.i();
        createEnchere(con,debutobjet, montant, de, sur);
    }
    
    public static Optional<Utilisateur> login(Connection con, int id, String nom, String prenom, String email, String pass, String codepostal) throws SQLException {
        try ( PreparedStatement pst = con.prepareStatement(
                "select fdbutilisateur.id as uid,"
                + " from fdbutilisateur "
                + " where fdbutilisateur.nom = ? and pass = ?")) {

            pst.setString(1, nom);
            pst.setString(2, pass);
            ResultSet res = pst.executeQuery();
            if (res.next()) {
                System.out.println("Login");
            } else {
                return Optional.empty();
            }
        }
        return null;
    }

    public static void createSchemaInitial(Connection con) throws SQLException, IOException {
        deleteSchema(con);
        createSchema(con);
        createUtilisateur(con, "Collado", "Ivan", "pass", "ivan.collado@insa-strasbourg.fr", "67000");
        createUtilisateur(con, "Derrien", "Léa", "mdp", "lea.derrien@insa-strasbourg.fr", "54000");
        createCategorie(con, "Meubles");
        createCategorie(con, "Vêtements");
        createCategorie(con, "Multimédias");
        createCategorie(con, "Alcools");
        createCategorie(con, "Sports");
        Timestamp debut = new Timestamp(2022-1900, 10-1, 25, 12, 0, 0, 0);
        Timestamp debut2 = new Timestamp(2022-1900, 11-1, 15, 14, 0, 0, 0);
        Timestamp fin = new Timestamp(2023-1900, 11-1, 28, 12, 0, 0, 0);
        createObjet(con, "Table", "Table basse", debut, fin, 30, 1, 1);
        createObjet(con, "Lit", "Lit simples avec sommier en lattes", debut, fin, 80, 1, 1);
        createObjet(con, "Robe", "Robe noire de gala", debut, fin, 60, 2, 2);
        createObjet(con, "Pantalon", "Pantalon en jeans", debut, fin, 25, 2, 2);
        createObjet(con, "Télévision", "Télévision avec écran plat", debut, fin, 100, 3, 1);
        createObjet(con, "Téléphone", "iPhone 14", debut, fin, 600, 3, 2);
        createObjet(con, "Vodka", "Vodka Uranov", debut, fin, 7, 4, 2);
        createObjet(con, "Rhum", "Rhum blanc Montajo 37 degrés", debut, fin, 7, 4, 1);
        createObjet(con, "Ballon", "Ballon de handball", debut, fin, 10, 5, 2);
        createObjet(con, "Baudrier", "Baudrier d'escalade taille M", debut, fin, 40, 5, 1);
        createEnchere(con, debut2, 35, 2, 1);
        createEnchere(con, debut2, 110, 2, 5);
        createEnchere(con, debut2, 650, 1, 6);
        createEnchere(con, debut2, 10, 1, 7);
    }

    public static void Menu(Connection con) throws IOException, ClassNotFoundException {
        boolean continuer = true;
        while (continuer == true) {
            System.out.println("\nVeuillez indiquer l'action que vous souhaitez réaliser :");
            System.out.println("1 --> Créer un schéma");
            System.out.println("2 --> Supprimmer le schéma");
            System.out.println("3 --> Ajouter un utilisateur");
            System.out.println("4 --> Afficher la liste des utilisateurs");
            System.out.println("5 --> Ajouter une categorie");
            System.out.println("6 --> Afficher la liste des categories");
            System.out.println("7 --> Ajouter une enchère");
            System.out.println("8 --> Afficher la liste des encheres");
            System.out.println("9 --> Ajouter un objet");
            System.out.println("10 --> Afficher la liste des objets");
            System.out.println("11 --> Chercher objet par mot clé");
            System.out.println("99 --> Quitter");
            int reponse = -1;
            while (reponse < 0) {
                reponse = Lire.i();
            }
            try {
                switch (reponse) {
                    case 1:
                        createSchema(con);
                        System.out.println("Vous venez de créer un schéma");
                        break;
                    case 2:
                        deleteSchema(con);
                        System.out.println("Vous venez de supprimer le schéma");
                        break;
                    case 3:
                        LireUtilisateur(con);
                        System.out.println("Vous avez créer un utilisateur");
                        break;
                    case 4:
                        System.out.println("Voici la liste des utilisateurs");
                        AfficherUtilisateurs(con);
                        break;
                    case 5:
                        LireCategorie(con);
                        System.out.println("La catégorie à été créee");
                        break;
                    case 6:
                        System.out.println("Voici la liste des catégories");
                        AfficherCategorie(con);
                        break;
                    case 7:
                        LireEnchere(con);
                        System.out.println("Votre enchère a été créée");
                        break;
                    case 8:
                        System.out.println("Voici la liste des enchères");
                        AfficherEncheres(con);
                        break;
                    case 9:
                        LireObjet(con);
                        System.out.println("L'objet a été créé");
                        break;
                    case 10:
                        System.out.println("Voici la liste des objets");
                        AfficherObjets(con);
                        break;
                    case 11:
                        System.out.println("Mot(s) clé(s) :");
                        ChercherObjetsMotCle(con);
                        break;
                    case 99:
                        continuer = false;
                        System.out.println("Vous venez de quitter le menu");
                        break;
                    default:
                        System.out.println("Choisir une action");
                        break;
                }
            } catch (SQLException ex) {
                throw new Error(ex);
            }
        }
    }

    public static void main(String[] args) throws IOException {
        try {
            Connection con = defautConnect();
            createSchemaInitial(con);
            Menu(con);

        } catch (ClassNotFoundException ex) {
            throw new Error(ex);
        } catch (SQLException ex) {
            throw new Error(ex);
        }
    }
}
// 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