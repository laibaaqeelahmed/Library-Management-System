public class Main {

    private static final int TOTAL_TESTS = 8;

    public static void main(String[] args) {

        displayWelcome();

        LibrarySystem library = new LibrarySystem();

        setupLibrary(library);
        demonstrateBorrowing(library);
        demonstrateSearch(library);
        demonstrateUtilities(library);
        demonstrateReturning(library);
        displayStatistics(library);

        System.out.println("\n" + "=".repeat(50));

        boolean testsPassed = runTests(library);

        if (!testsPassed) {
            System.exit(1);
        }
    }

    private static void displayWelcome() {
        System.out.println("=== Library Management System ===");
        System.out.println("Demonstrating Refactored Functionality\n");
    }

    private static void setupLibrary(LibrarySystem library) {

        library.addBook("978-0134685991", "Effective Java",
                "Joshua Bloch", "Programming");

        library.addBook("978-0596009205", "Head First Design Patterns",
                "Eric Freeman", "Programming");

        library.addBook("978-0321356680", "Effective C++",
                "Scott Meyers", "Programming");

        library.addMember("M001", "Ahmed Ali",
                "ahmed.ali@email.com", "1234567890", "123 Gulberg Lane");

        library.addMember("M002", "Fatima Khan",
                "fatima.khan@email.com", "0987654321", "456 DHA Avenue");
    }

    private static void demonstrateBorrowing(LibrarySystem library) {

        System.out.println("\n=== Borrowing Books ===");

        library.borrowBook("M001", "978-0134685991", 14,
                null, null, null, null, false, null, null, null);

        library.borrowBook("M003", "978-0596009205", 7,
                "Hassan Malik", "hassan.malik@email.com",
                "5551234567", "789 Johar Town",
                true, null, null, null);
    }

    private static void demonstrateSearch(LibrarySystem library) {

        System.out.println("\n=== Search Results ===");

        library.searchBooks("Java").forEach(System.out::println);
    }

    private static void demonstrateUtilities(LibrarySystem library) {

        System.out.println("\n=== Book Utilities Demo ===");

        BookUtilities utilities = new BookUtilities();

        Book book = library.getBook("978-0134685991");

        System.out.println(utilities.generateBookReport(book));

        System.out.println("\n=== Member Validation Demo ===");

        MemberValidator validator = new MemberValidator();

        Member member = library.getMember("M001");

        double risk = validator.calculateMemberRisk(member);

        System.out.println("Member risk score: " + risk);
    }

    private static void demonstrateReturning(LibrarySystem library) {

        System.out.println("\n=== Returning Books ===");

        library.returnBook("978-0134685991");
        library.returnBook("978-0596009205");
    }

    private static void displayStatistics(LibrarySystem library) {

        System.out.println("\n=== System Statistics ===");

        System.out.println("Total Books: " + library.getTotalBooks());
        System.out.println("Total Members: " + library.getTotalMembers());
        System.out.println("Available Books: " + library.getAvailableBooks());
    }

    private static boolean runTests(LibrarySystem library) {

        System.out.println("Running Unit Tests...\n");

        int passedTests = 0;

        if (testBookManagement(library)) {
            passedTests++;
        }

        if (testMemberManagement(library)) {
            passedTests++;
        }

        if (testBorrowing(library)) {
            passedTests++;
        }

        if (testReturning(library)) {
            passedTests++;
        }

        if (testSearch(library)) {
            passedTests++;
        }

        if (testMemberValidation()) {
            passedTests++;
        }

        if (testBookUtilities(library)) {
            passedTests++;
        }

        if (testSystemStatistics(library)) {
            passedTests++;
        }

        displayTestSummary(passedTests);

        return passedTests == TOTAL_TESTS;
    }

    private static boolean testBookManagement(LibrarySystem library) {

        library.addBook("TEST001", "Test Book",
                "Test Author", "Fiction");

        Book book = library.getBook("TEST001");

        return reportTest(1, "Book management",
                book != null && book.getTitle().equals("Test Book"));
    }

    private static boolean testMemberManagement(LibrarySystem library) {

        library.addMember("TEST001", "Zainab Hussain",
                "zainab.test@email.com", "1111111111",
                "Test Colony Karachi");

        Member member = library.getMember("TEST001");

        return reportTest(2, "Member management",
                member != null && member.getName().equals("Zainab Hussain"));
    }

    private static boolean testBorrowing(LibrarySystem library) {

        Book book = library.getBook("TEST001");

        boolean result = library.borrowBook("TEST001", "TEST001", 14,
                null, null, null, null, false, null, null, null);

        return reportTest(3, "Book borrowing",
                result && !book.isAvailable());
    }

    private static boolean testReturning(LibrarySystem library) {

        Book book = library.getBook("TEST001");

        boolean result = library.returnBook("TEST001");

        return reportTest(4, "Book returning",
                result && book.isAvailable());
    }

    private static boolean testSearch(LibrarySystem library) {

        boolean result = library.searchBooks("Test").size() > 0;

        return reportTest(5, "Book search", result);
    }

    private static boolean testMemberValidation() {

        MemberValidator validator = new MemberValidator();

        boolean result = validator.validateMemberForRegistration(
                "Muhammad Usman", "usman.valid@email.com",
                "1234567890", "Model Town Lahore");

        return reportTest(6, "Member validation", result);
    }

    private static boolean testBookUtilities(LibrarySystem library) {

        BookUtilities utilities = new BookUtilities();

        Book book = library.getBook("TEST001");

        String report = utilities.generateBookReport(book);

        return reportTest(7, "Book utilities",
                report.contains("BOOK REPORT"));
    }

    private static boolean testSystemStatistics(LibrarySystem library) {

        boolean result = library.getTotalBooks() >= 4
                && library.getTotalMembers() >= 3;

        return reportTest(8, "System statistics", result);
    }

    private static boolean reportTest(int testNumber,
            String testName, boolean passed) {

        if (passed) {
            System.out.println("✓ Test " + testNumber
                    + " PASSED: " + testName);
        } else {
            System.out.println("✗ Test " + testNumber
                    + " FAILED: " + testName);
        }

        return passed;
    }

    private static void displayTestSummary(int passedTests) {

        System.out.println("\n=== Test Summary ===");
        System.out.println("Total Tests: " + TOTAL_TESTS);
        System.out.println("Passed: " + passedTests);
        System.out.println("Failed: " + (TOTAL_TESTS - passedTests));

        System.out.println("Success Rate: "
                + (passedTests * 100 / TOTAL_TESTS) + "%");
    }
}