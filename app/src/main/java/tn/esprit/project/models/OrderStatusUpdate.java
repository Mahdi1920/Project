package tn.esprit.project.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "order_status_updates")
public class OrderStatusUpdate {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "order_id")
    private int orderId;

    @ColumnInfo(name = "status")
    private String status;

    @ColumnInfo(name = "timestamp")
    private long timestamp;

    @ColumnInfo(name = "latitude")
    private Double latitude;

    @ColumnInfo(name = "longitude")
    private Double longitude;

    // constructors
    public OrderStatusUpdate() {}

    public OrderStatusUpdate(int orderId, String status, long timestamp, Double latitude, Double longitude) {
        this.orderId = orderId;
        this.status = status;
        this.timestamp = timestamp;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public OrderStatusUpdate(int id, int orderId, String status, long timestamp, Double latitude, Double longitude) {
        this.id = id;
        this.orderId = orderId;
        this.status = status;
        this.timestamp = timestamp;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // getters/setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
}

