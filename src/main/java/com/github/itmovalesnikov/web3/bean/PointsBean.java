package com.github.itmovalesnikov.web3.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.fraction.BigFraction;
import org.hibernate.Session;
import org.hibernate.query.Query;

import com.github.itmovalesnikov.web3.jmx.JmxRegistry;
import com.github.itmovalesnikov.web3.model.BPoint;
import com.github.itmovalesnikov.web3.model.Point;
import com.github.itmovalesnikov.web3.utils.BigFractionUtils;
import com.github.itmovalesnikov.web3.utils.HibernateUtil;
import com.github.itmovalesnikov.web3.utils.SessionUtils;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;

/**
 * A managed bean that handles point validation and storage for the web application.
 * Manages coordinate values (x, y) and radius (r) for points, validates them,
 * and stores both temporary points and persistent points in the database.
 */
@Named("pointsBean")
@SessionScoped
public class PointsBean implements Serializable {

    /** X coordinate of the point */
    private BigFraction x = BigFraction.ZERO;
    
    /** Y coordinate of the point */
    private BigFraction y = BigFraction.ZERO;
    
    /** Radius value for hit detection */
    private BigFraction r = BigFraction.ONE;
    
    /** List of points for current session */
    private final List<Point> points = new ArrayList<>();

    /**
     * Submits the current coordinates to validate hit detection and store in database.
     * Creates both a temporary Point object and a persistent BPoint object in the database.
     */
    public void submit() {
        Point point = new Point(x, y, r);
        points.add(point);
        JmxRegistry.recordPoint(SessionUtils.getSessionId(), point.isHit());
        JmxRegistry.updateRadius(r);

        BPoint bpoint = new BPoint(x, y, r, SessionUtils.getSessionId());
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.persist(bpoint);
            session.getTransaction().commit();
        }
    }

    /**
     * Gets the X coordinate as a decimal string.
     *
     * @return X coordinate as a decimal string representation
     */
    public String getX() {
        return BigFractionUtils.toDecimalString(x);
    }

    /**
     * Sets the X coordinate from a string representation.
     *
     * @param xStr String representation of the X coordinate
     */
    public void setX(String xStr) {
        this.x = BigFractionUtils.fromDecimalString(xStr);
    }

    /**
     * Gets the Y coordinate as a decimal string.
     *
     * @return Y coordinate as a decimal string representation
     */
    public String getY() {
        return BigFractionUtils.toDecimalString(y);
    }

    /**
     * Sets the Y coordinate from a string representation.
     *
     * @param yStr String representation of the Y coordinate
     */
    public void setY(String yStr) {
        this.y = BigFractionUtils.fromDecimalString(yStr);
    }

    /**
     * Gets the radius as a decimal string.
     *
     * @return Radius as a decimal string representation
     */
    public String getR() {
        return BigFractionUtils.toDecimalString(r);
    }

    /**
     * Sets the radius from a string representation.
     *
     * @param rStr String representation of the radius
     */
    public void setR(String rStr) {
        this.r = BigFractionUtils.fromDecimalString(rStr);
    }

    /**
     * Retrieves all points stored in the database for the current session.
     *
     * @return List of BPoint objects associated with the current session
     */
    public List<BPoint> getPoints() {
        List<BPoint> points = new ArrayList<>();
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<BPoint> query = session.createQuery(
                    "FROM BPoint p WHERE p.sessionId = :sessionId", BPoint.class);
            query.setParameter("sessionId", SessionUtils.getSessionId());
            points = query.list();
        }
        return points;
    }
}
