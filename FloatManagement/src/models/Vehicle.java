package models;
import java.util.UUID;
public class Vehicle {

    private UUID vehicleID;
    private String brand;
    private String model;
    private int yearOfProduction;
    private String fuelType;
    private int mileage;
    private boolean isAvailable;

    public Vehicle(String brand, String model, int yearOfProduction, String fuelType, int mileage, boolean isAvailable) {
        this.vehicleID = UUID.randomUUID();
        this.brand = brand;
        this.model = model;
        this.yearOfProduction = yearOfProduction;
        this.fuelType = fuelType;
        this.mileage = mileage;
        this.isAvailable = isAvailable;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getYearOfProduction() {
        return yearOfProduction;
    }

    public void setYearOfProduction(int yearOfProduction) {
        this.yearOfProduction = yearOfProduction;
    }

    public String getFuelType() {
        return fuelType;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }

    public int getMileage() {
        return mileage;
    }

    public void setMileage(int mileage) {
        this.mileage = mileage;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    @Override
    public String toString() {
        return "Vehicle{" +
                "brand='" + brand + '\'' +
                ", model='" + model + '\'' +
                ", yearOfProduction=" + yearOfProduction +
                ", fuelType='" + fuelType + '\'' +
                ", mileage=" + mileage +
                ", isAvailable=" + isAvailable +
                '}';
    }
}
