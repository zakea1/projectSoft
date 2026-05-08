package Domain_Layer;

import java.time.Duration;
import java.time.LocalDateTime;

public class TimeSlot {
    private LocalDateTime start;
    private LocalDateTime end;
    private boolean booked;
    private String bookedBy;
    private AppointmentType type;
    private int participants;

    public TimeSlot(LocalDateTime start, LocalDateTime end) {
        this.start = start;
        this.end = end;
        this.booked = false;
        this.bookedBy = "";
        this.type = null;
        this.participants = 0;
    }

    public LocalDateTime getStart() { return start; }
    public LocalDateTime getEnd() { return end; }
    public boolean isBooked() { return booked; }
    public String getBookedBy() { return bookedBy; }
    public AppointmentType getType() { return type; }
    public int getParticipants() { return participants; }

    public void book(String email, AppointmentType type, int participants) {
        this.booked = true;
        this.bookedBy = email;
        this.type = type;
        this.participants = participants;
    }

    public void cancel() {
        this.booked = false;
        this.bookedBy = "";
        this.type = null;
        this.participants = 0;
    }

   
    public long getDurationMinutes() {
        return Duration.between(start, end).toMinutes();
    }
}
