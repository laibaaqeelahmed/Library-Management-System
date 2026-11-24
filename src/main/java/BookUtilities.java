import java.util.Date;

public class BookUtilities {

    private static final long MILLISECONDS_PER_DAY =
            1000L * 60 * 60 * 24;

    private static final double DEFAULT_FINE_RATE = 0.5;
    private static final double REFERENCE_FINE_RATE = 1.0;
    private static final double CHILDREN_FINE_RATE = 0.25;

    public boolean isBookOverdue(Book book) {
        return calculateOverdueDays(book) > 0;
    }

    public long calculateOverdueDays(Book book) {
        if (book.getDueDate() == null) {
            return 0;
        }

        Date today = new Date();
        long difference = today.getTime() - book.getDueDate().getTime();

        return difference > 0 ? difference / MILLISECONDS_PER_DAY : 0;
    }

    public String generateBookReport(Book book) {
        StringBuilder report = new StringBuilder();

        report.append("=== BOOK REPORT ===\n");
        report.append("ISBN: ").append(book.getIsbn()).append("\n");
        report.append("Title: ").append(book.getTitle()).append("\n");
        report.append("Author: ").append(book.getAuthor()).append("\n");
        report.append("Category: ").append(book.getCategory()).append("\n");
        report.append("Available: ").append(book.isAvailable() ? "Yes" : "No").append("\n");

        if (!book.isAvailable()) {
            report.append("Borrower: ").append(book.getBorrowerMemberId()).append("\n");
            report.append("Due Date: ").append(book.getDueDate()).append("\n");

            long overdueDays = calculateOverdueDays(book);

            if (overdueDays > 0) {
                double fineRate = getFineRate(book.getCategory());
                double totalFine = overdueDays * fineRate;

                report.append("Overdue Days: ").append(overdueDays).append("\n");
                report.append("Fine Amount: $").append(totalFine).append("\n");
            }
        }

        report.append("==================\n");
        return report.toString();
    }

    private double getFineRate(String category) {
        if ("Reference".equals(category)) {
            return REFERENCE_FINE_RATE;
        }

        if ("Children".equals(category)) {
            return CHILDREN_FINE_RATE;
        }

        return DEFAULT_FINE_RATE;
    }

    public boolean isPopularCategory(String category) {
        switch (category) {
            case "Fiction":
            case "Non-Fiction":
            case "Children":
                return true;

            case "Reference":
            default:
                return false;
        }
    }
}