package main.java.com.parcattractions.utils;

import  java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * File d'attente thread-safe pour gérer les visiteurs
 * Utilise ReentrantLock pour une meilleure performance
 * 
 * @param <T> Type d'éléments dans la file (généralement Visiteur)
 */
public class FileAttente<T> {
    
    private final Queue<T> queue;
    private final ReentrantLock lock;
    private final Condition nonVide;
    private final int capaciteMax;
    
    /**
     * Constructeur avec capacité illimitée
     */
    public FileAttente() {
        this(Integer.MAX_VALUE);
    }
    
    /**
     * Constructeur avec capacité maximale
     * @param capaciteMax Capacité maximale de la file
     */
    public FileAttente(int capaciteMax) {
        this.queue = new LinkedList<>();
        this.lock = new ReentrantLock(true); // Fair lock
        this.nonVide = lock.newCondition();
        this.capaciteMax = capaciteMax;
    }
    
    /**
     * Ajoute un élément à la file
     * @param element Élément à ajouter
     * @throws IllegalStateException Si la file est pleine
     */
    public void ajouter(T element) {
        lock.lock();
        try {
            if (queue.size() >= capaciteMax) {
                throw new IllegalStateException("File d'attente pleine");
            }
            queue.add(element);
            nonVide.signal(); // Signaler qu'un élément est disponible
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * Retire et retourne le premier élément de la file
     * Bloque si la file est vide
     * 
     * @return Premier élément
     * @throws InterruptedException Si le thread est interrompu pendant l'attente
     */
    public T retirer() throws InterruptedException {
        lock.lock();
        try {
            while (queue.isEmpty()) {
                nonVide.await(); // Attendre qu'un élément soit ajouté
            }
            return queue.poll();
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * Retire un élément avec timeout
     * @param timeoutMs Timeout en millisecondes
     * @return Élément ou null si timeout
     * @throws InterruptedException Si interrompu
     */
    public T retirerAvecTimeout(long timeoutMs) throws InterruptedException {
        lock.lock();
        try {
            if (queue.isEmpty()) {
                nonVide.await(timeoutMs, java.util.concurrent.TimeUnit.MILLISECONDS);
            }
            return queue.poll();
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * Regarde le premier élément sans le retirer
     * @return Premier élément ou null si vide
     */
    public T regarder() {
        lock.lock();
        try {
            return queue.peek();
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * Retourne la taille actuelle de la file
     * @return Nombre d'éléments
     */
    public int getTaille() {
        lock.lock();
        try {
            return queue.size();
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * Vérifie si la file est vide
     * @return Vrai si vide
     */
    public boolean estVide() {
        lock.lock();
        try {
            return queue.isEmpty();
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * Vérifie si la file est pleine
     * @return Vrai si pleine
     */
    public boolean estPleine() {
        lock.lock();
        try {
            return queue.size() >= capaciteMax;
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * Vide complètement la file
     */
    public void vider() {
        lock.lock();
        try {
            queue.clear();
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * @return Capacité maximale
     */
    public int getCapaciteMax() {
        return capaciteMax;
    }
    
    /**
     * @return Taux de remplissage (0.0 à 1.0)
     */
    public double getTauxRemplissage() {
        lock.lock();
        try {
            if (capaciteMax == Integer.MAX_VALUE) {
                return 0.0;
            }
            return (double) queue.size() / capaciteMax;
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * Vérifie si un élément est dans la file
     * @param element Élément à chercher
     * @return Vrai si présent
     */
    public boolean contient(T element) {
        lock.lock();
        try {
            return queue.contains(element);
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * Retire un élément spécifique de la file
     * @param element Élément à retirer
     * @return Vrai si retiré avec succès
     */
    public boolean retirer(T element) {
        lock.lock();
        try {
            return queue.remove(element);
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * Représentation textuelle
     */
    @Override
    public String toString() {
        lock.lock();
        try {
            return String.format("FileAttente[taille=%d, capacite=%d, remplissage=%.1f%%]", 
                queue.size(), 
                capaciteMax == Integer.MAX_VALUE ? 0 : capaciteMax,
                getTauxRemplissage() * 100
            );
        } finally {
            lock.unlock();
        }
    }
}
