package services;

import entities.Question;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;

public class QuestionService implements IService<Question> {

    private static final EntityManagerFactory emf =
            Persistence.createEntityManagerFactory("myPU");

    private EntityManager getEM() {
        return emf.createEntityManager();
    }

    // ─── CRUD ─────────────────────────────────────────────────────

    @Override
    public void create(Question question) {
        EntityManager em = getEM();
        try {
            em.getTransaction().begin();
            if (question.getDateCreation() == null) {
                question.setDateCreation(LocalDateTime.now());
            }
            em.persist(question);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public void update(Question question) {
        EntityManager em = getEM();
        try {
            em.getTransaction().begin();
            em.merge(question);
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
            Question question = em.find(Question.class, (long) id);
            if (question != null) {
                em.remove(question);
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
    public Question getById(int id) {
        EntityManager em = getEM();
        try {
            return em.find(Question.class, (long) id);
        } finally {
            em.close();
        }
    }

    @Override
    public List<Question> getAll() {
        EntityManager em = getEM();
        try {
            return em.createQuery("SELECT q FROM Question q", Question.class).getResultList();
        } finally {
            em.close();
        }
    }

    // ─── Méthodes métier ──────────────────────────────────────────

    public List<Question> getByQuiz(long quizId) {
        EntityManager em = getEM();
        try {
            TypedQuery<Question> query = em.createQuery(
                    "SELECT q FROM Question q WHERE q.quiz.id = :quizId ORDER BY q.ordreAffichage ASC",
                    Question.class);
            query.setParameter("quizId", quizId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public List<Question> getByType(String typeQuestion) {
        EntityManager em = getEM();
        try {
            TypedQuery<Question> query = em.createQuery(
                    "SELECT q FROM Question q WHERE q.typeQuestion = :typeQuestion", Question.class);
            query.setParameter("typeQuestion", typeQuestion);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public List<Question> getByCours(int idCours) {
        EntityManager em = getEM();
        try {
            TypedQuery<Question> query = em.createQuery(
                    "SELECT q FROM Question q WHERE q.idCours = :idCours", Question.class);
            query.setParameter("idCours", idCours);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Réordonne les questions d'un quiz selon la liste d'IDs fournie.
     * L'index dans la liste détermine le nouvel ordreAffichage (commence à 1).
     */
    public void reorderQuestions(long quizId, List<Long> orderedIds) {
        EntityManager em = getEM();
        try {
            em.getTransaction().begin();
            for (int i = 0; i < orderedIds.size(); i++) {
                Question question = em.find(Question.class, orderedIds.get(i));
                if (question != null && question.getQuiz().getId().equals(quizId)) {
                    question.setOrdreAffichage(i + 1);
                    em.merge(question);
                }
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}