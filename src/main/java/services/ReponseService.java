package services;

import entities.Reponse;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import java.util.Collections;
import java.util.List;

public class ReponseService implements IService<Reponse> {

    private static final EntityManagerFactory emf =
            Persistence.createEntityManagerFactory("myPU");

    private EntityManager getEM() {
        return emf.createEntityManager();
    }

    // ─── CRUD ─────────────────────────────────────────────────────

    @Override
    public void create(Reponse reponse) {
        EntityManager em = getEM();
        try {
            em.getTransaction().begin();
            em.persist(reponse);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public void update(Reponse reponse) {
        EntityManager em = getEM();
        try {
            em.getTransaction().begin();
            em.merge(reponse);
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
            Reponse reponse = em.find(Reponse.class, (long) id);
            if (reponse != null) {
                em.remove(reponse);
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
    public Reponse getById(int id) {
        EntityManager em = getEM();
        try {
            return em.find(Reponse.class, (long) id);
        } finally {
            em.close();
        }
    }

    @Override
    public List<Reponse> getAll() {
        EntityManager em = getEM();
        try {
            return em.createQuery("SELECT r FROM Reponse r", Reponse.class).getResultList();
        } finally {
            em.close();
        }
    }

    // ─── Méthodes métier ──────────────────────────────────────────

    public List<Reponse> getByQuestion(long questionId) {
        EntityManager em = getEM();
        try {
            TypedQuery<Reponse> query = em.createQuery(
                    "SELECT r FROM Reponse r WHERE r.question.id = :questionId ORDER BY r.ordreAffichage ASC",
                    Reponse.class);
            query.setParameter("questionId", questionId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public List<Reponse> getCorrectAnswers(long questionId) {
        EntityManager em = getEM();
        try {
            TypedQuery<Reponse> query = em.createQuery(
                    "SELECT r FROM Reponse r WHERE r.question.id = :questionId AND r.estCorrecte = true",
                    Reponse.class);
            query.setParameter("questionId", questionId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Retourne les réponses d'une question dans un ordre aléatoire.
     * Utile pour éviter que les étudiants mémorisent la position des bonnes réponses.
     */
    public List<Reponse> shuffleReponses(long questionId) {
        List<Reponse> reponses = getByQuestion(questionId);
        Collections.shuffle(reponses);
        return reponses;
    }
}