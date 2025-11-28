package dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import entity.Alumno;

public class AlumnoDAO {
      private EntityManagerFactory emf = Persistence.createEntityManagerFactory("alumnosPU");

      public void insertar(Alumno alumno) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.persist(alumno);
        em.getTransaction().commit();
        em.close();
      }
    }