package services;

import entities.ResultatQuiz;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import java.util.List;

public class ResultatQuizService implements IService<ResultatQuiz> {

    private static final EntityManagerFactory emf =
            Persistence.createEntityManagerFactory("myPU");

    private EntityManager getEM() {
        return emf.createEntityManager();
    }

    // ─── CRUD ─────────────────────────────────────────────────────

    @Override
    public void create(ResultatQuiz resultat) {
        EntityManager em = getEM();
        try {
            em.getTransaction().begin();
            em.persist(resultat);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public void update(ResultatQuiz resultat) {
        EntityManager em = getEM();
        try {
            em.getTransaction().begin();
            em.merge(resultat);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public void delete(int id) {
        EntityManager em = getEM();
        try {
            em.getTransaction().begin();
            ResultatQuiz resultat = em.find(ResultatQuiz.class, (long) id);
            if (resultat != null) {
                em.remove(resultat);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public ResultatQuiz getById(int id) {
        EntityManager em = getEM();
        try {
            return em.find(ResultatQuiz.class, (long) id);
        } finally {
            em.close();
        }
    }

    @Override
    public List<ResultatQuiz> getAll() {
        EntityManager em = getEM();
        try {
            return em.createQuery("SELECT r FROM ResultatQuiz r", ResultatQuiz.class).getResultList();
        } finally {
            em.close();
        }
    }

    // ─── Méthodes métier ──────────────────────────────────────────

    public List<ResultatQuiz> getByEtudiant(int idEtudiant) {
        EntityManager em = getEM();
        try {
            TypedQuery<ResultatQuiz> query = em.createQuery(
                    "SELECT r FROM ResultatQuiz r WHERE r.idEtudiant = :idEtudiant ORDER BY r.datePassation DESC",
                    ResultatQuiz.class);
            query.setParameter("idEtudiant", idEtudiant);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public List<ResultatQuiz> getByQuiz(long quizId) {
        EntityManager em = getEM();
        try {
            TypedQuery<ResultatQuiz> query = em.createQuery(
                    "SELECT r FROM ResultatQuiz r WHERE r.quiz.id = :quizId ORDER BY r.datePassation DESC",
                    ResultatQuiz.class);
            query.setParameter("quizId", quizId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Vérifie si un étudiant a déjà passé un quiz au moins une fois.
     */
    public boolean hasAlreadyPassed(int idEtudiant, long quizId) {
        EntityManager em = getEM();
        try {
            TypedQuery<Long> query = em.createQuery(
                    "SELECT COUNT(r) FROM ResultatQuiz r WHERE r.idEtudiant = :idEtudiant AND r.quiz.id = :quizId",
                    Long.class);
            query.setParameter("idEtudiant", idEtudiant);
            query.setParameter("quizId", quizId);
            return query.getSingleResult() > 0;
        } finally {
            em.close();
        }
    }

    /**
     * Calcule la moyenne des scores pour un quiz donné (sur 100).
     * Retourne 0.0 si aucun résultat n'existe.
     */
    public double getMoyenneScore(long quizId) {
        EntityManager em = getEM();
        try {
            TypedQuery<Double> query = em.createQuery(
                    "SELECT AVG(r.score) FROM ResultatQuiz r WHERE r.quiz.id = :quizId",
                    Double.class);
            query.setParameter("quizId", quizId);
            Double avg = query.getSingleResult();
            return avg != null ? avg : 0.0;
        } finally {
            em.close();
        }
    }

    /**
     * Retourne le nombre de tentatives d'un étudiant pour un quiz donné.
     */
    public int countTentatives(int idEtudiant, long quizId) {
        EntityManager em = getEM();
        try {
            TypedQuery<Long> query = em.createQuery(
                    "SELECT COUNT(r) FROM ResultatQuiz r WHERE r.idEtudiant = :idEtudiant AND r.quiz.id = :quizId",
                    Long.class);
            query.setParameter("idEtudiant", idEtudiant);
            query.setParameter("quizId", quizId);
            return query.getSingleResult().intValue();
        } finally {
            em.close();
        }
    }

    /**
     * Retourne le meilleur score d'un étudiant pour un quiz donné.
     * Retourne 0.0 si aucun résultat.
     */
    public double getMeilleurScore(int idEtudiant, long quizId) {
        EntityManager em = getEM();
        try {
            TypedQuery<Double> query = em.createQuery(
                    "SELECT MAX(r.score) FROM ResultatQuiz r WHERE r.idEtudiant = :idEtudiant AND r.quiz.id = :quizId",
                    Double.class);
            query.setParameter("idEtudiant", idEtudiant);
            query.setParameter("quizId", quizId);
            Double max = query.getSingleResult();
            return max != null ? max : 0.0;
        } finally {
            em.close();
        }
    }
}