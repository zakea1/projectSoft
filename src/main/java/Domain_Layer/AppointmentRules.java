package Domain_Layer;

public class AppointmentRules {

    public static int getMaxDuration(AppointmentType type) {
        switch (type) {
            case URGENT:
                return 60;

            case FOLLOW_UP:
                return 20;

            case ASSESSMENT:
                return 45;

            case GROUP:
                return 60;

            case INDIVIDUAL:
                return 30;

            case VIRTUAL:
                return 30;

            case IN_PERSON:
                return 30;

            default:
                return 30;
        }
    }

    public static boolean validateParticipants(AppointmentType type, int participants) {
        if (type == AppointmentType.GROUP) {
            return participants > 1;
        } else {
            return participants == 1;
        }
    }
}