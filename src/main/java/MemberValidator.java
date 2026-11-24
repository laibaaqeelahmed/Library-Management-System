import java.util.Date;

public class MemberValidator {

    private static final int MIN_NAME_LENGTH = 2;
    private static final int MIN_PHONE_LENGTH = 10;
    private static final int MIN_ADDRESS_LENGTH = 5;

    private static final int MAX_BORROWED_BOOKS = 5;
    private static final double MAX_ALLOWED_FINES = 25.0;
    private static final int MAX_BORROW_HISTORY = 50;
    private static final int MEMBERSHIP_MONTHS = 6;

    private static final double BOOK_RISK_FACTOR = 0.1;
    private static final double FINE_RISK_FACTOR = 0.05;
    private static final double BORROW_RISK_FACTOR = 0.02;

    private static final double HIGH_BORROW_RISK = 1.5;
    private static final double HIGH_FINE_RISK = 2.0;
    private static final double NEW_MEMBER_RISK = 1.0;

    private static final int HIGH_BORROW_THRESHOLD = 3;
    private static final double HIGH_FINE_THRESHOLD = 10.0;
    private static final long NEW_MEMBER_DAYS = 30;
    private static final long MILLISECONDS_PER_DAY = 1000L * 60 * 60 * 24;

    public boolean validateMemberForBorrowing(
            Member member, String newName, String newEmail,
            String newPhone, String newAddress, int maxBooks,
            double maxFines, boolean checkHistory, boolean updateInfo,
            boolean sendNotification, String notificationMessage) {

        if (member == null || !validateBasicInfo(
                member.getName(), member.getEmail(),
                member.getPhoneNumber(), member.getAddress())) {
            return false;
        }

        if (member.getBorrowedBooks().size() >= MAX_BORROWED_BOOKS) {
            return false;
        }

        if (member.getTotalFines() > MAX_ALLOWED_FINES) {
            return false;
        }

        if (checkHistory && member.getBorrowCount() > MAX_BORROW_HISTORY) {
            return false;
        }

        return true;
    }

    public boolean validateMemberForRegistration(
            String name, String email, String phone, String address) {

        return validateBasicInfo(name, email, phone, address);
    }

    private boolean validateBasicInfo(
            String name, String email, String phone, String address) {

        if (name == null || name.trim().length() < MIN_NAME_LENGTH) {
            return false;
        }

        if (email == null || !email.contains("@") || !email.contains(".")) {
            return false;
        }

        if (phone == null || phone.length() < MIN_PHONE_LENGTH) {
            return false;
        }

        if (address == null || address.trim().length() < MIN_ADDRESS_LENGTH) {
            return false;
        }

        return true;
    }

    public double calculateMemberRisk(Member member) {

        if (member == null) {
            return 0.0;
        }

        double risk = 0.0;

        risk += member.getBorrowedBooks().size() * BOOK_RISK_FACTOR;
        risk += member.getTotalFines() * FINE_RISK_FACTOR;
        risk += member.getBorrowCount() * BORROW_RISK_FACTOR;

        if (member.getBorrowedBooks().size() > HIGH_BORROW_THRESHOLD) {
            risk += HIGH_BORROW_RISK;
        }

        if (member.getTotalFines() > HIGH_FINE_THRESHOLD) {
            risk += HIGH_FINE_RISK;
        }

        Date membershipDate = member.getMembershipDate();

        if (membershipDate != null) {
            long daysSinceMembership =
                    (new Date().getTime() - membershipDate.getTime())
                    / MILLISECONDS_PER_DAY;

            if (daysSinceMembership < NEW_MEMBER_DAYS) {
                risk += NEW_MEMBER_RISK;
            }
        }

        return risk;
    }
}