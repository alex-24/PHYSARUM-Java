/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package physarum_2d.model;

/**
 *
 * @author Alexis Cassion
 */
public class Vector {
    private double x;
    private double y;

    public Vector(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Vector(Vector a, Vector b){
        this.x = a.x - b.x;
        this.y = a.y - b.y;
    }
    
    public static Vector randomVector(double minX, double maxX, double minY, double maxY) {
        Vector result = new Vector(
                (maxX - minX) * Math.random() + minX,
                (maxY - minY) * Math.random() + minY
        );
        
        return result;
    }
    
    public static Vector randomUnitVect() {
        return new Vector(1, 1).rotate(Math.random() * Math.PI).toUnitVect();
    }
    
    
    public Vector orientTowardsCoord(Vector v) {
        return this.sub(v).scale(-1);
    }
        

    public Vector add(Vector v){
        this.x += v.x;
        this.y += v.y;
        return this;
    }

    public Vector sub(Vector v){
        this.x -= v.x;
        this.y -= v.y;
        return this;
    }

    public double scalarProduct(Vector v){
        return v.x * this.x + v.y * this.y;
    }

    public double vectProductSign(Vector v){
        return (this.x * v.y - v.x * this.y > 0)? 1 : -1;
    }

    public double vectProduct(Vector v){
        // x*v.y + y*v.x

        double norm1 = this.norm();
        double norm2 = v.norm();
        double angle = Math.acos(this.scalarProduct(v) / (norm1 * norm2) );

        return angle * vectProductSign(v);
    }

    public Vector scale(double epsilon){
        this.x *= epsilon;
        this.y *= epsilon;
        return this;
    }

    public Vector rotate(double angle){
        double tmpX = this.x;

        this.x = this.x * Math.cos(angle) - this.y * Math.sin(angle);
        this.y = tmpX * Math.sin(angle) + this.y * Math.cos(angle);
        return this;	
    }

    public double norm(){
        return Math.sqrt(this.x * this.x + this.y * this.y);
    }		

    public Vector clone(){
        return new Vector(this.x, this.y);
    }

    public Vector toUnitVect(){
        double n = norm();
        this.x = this.x / n;
        this.y = this.y / n;
        return this;
    }

    public static double angle(Vector v1, Vector v2) {
        double d = v1.scalarProduct(v2) /(v1.norm() * v2.norm());
        d = Math.max(Math.min(d, 1.), -1.);

//		System.out.println(v1.vectProductSign(v2));

        return v1.vectProductSign(v2) * Math.acos(d);
    }
    

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
                return true;
        if (obj == null || getClass() != obj.getClass())
                return false;
        
        Vector other = (Vector) obj;
        if (Double.doubleToLongBits(this.x) != Double.doubleToLongBits(other.x))
                return false;
        if (Double.doubleToLongBits(y) != Double.doubleToLongBits(other.y))
                return false;
        
        return true;
    }

    @Override
    public String toString() {
        return "v:(" + this.x + "," + this.y + ") ";
    }
}
