import java.util.*;
import java.text.SimpleDateFormat;

public class LibrarySystem {

    private static final int MAX_BORROWED_BOOKS = 5;
    private static final double MAX_ALLOWED_FINES = 25.0;
    private static final long MILLISECONDS_PER_DAY =
            1000L * 60 * 60 * 24;

    private Map<String, Book> books;
    private Map<String, Member> members;
    private List<String> transactionHistory;
    private Map<String, Double> categoryFines;
    private SimpleDateFormat dateFormat;

    public LibrarySystem() {
        books = new HashMap<>();
        members = new HashMap<>();
        transactionHistory = new ArrayList<>();
        categoryFines = new HashMap<>();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        initializeCategoryFines();
    }

    private void initializeCategoryFines() {
        categoryFines.put("Fiction", 0.5);
        categoryFines.put("Non-Fiction", 0.75);
        categoryFines.put("Reference", 1.0);
        categoryFines.put("Children", 0.25);
    }

    public boolean borrowBook(String memberId, String isbn, int borrowDays,
            String memberName, String memberEmail, String memberPhone,
            String memberAddress, boolean isNewMember, String bookTitle,
            String bookAuthor, String bookCategory) {

        Member member = findOrCreateMember(memberId, memberName,
                memberEmail, memberPhone, memberAddress, isNewMember);

        if (member == null || !canBorrow(member)) {
            return false;
        }

        Book book = findOrCreateBook(isbn, bookTitle, bookAuthor, bookCategory);

        if (book == null || !book.isAvailable()) {
            if (book != null) {
                showBookAvailability(book);
            }
            return false;
        }

        Date dueDate = calculateDueDate(borrowDays);

        processBorrowing(book, member, isbn, memberId, dueDate);

        displayBorrowingConfirmation(member, book, isbn, borrowDays, dueDate);

        return true;
    }

    private Member findOrCreateMember(String memberId, String name,
            String email, String phone, String address, boolean isNewMember) {

        Member member = members.get(memberId);

        if (member != null) {
            return member;
        }

        if (!isNewMember) {
            System.out.println("Member not found: " + memberId);
            return null;
        }

        if (!validateMemberDetails(name, email, phone, address)) {
            return null;
        }

        member = new Member(memberId, name, email, phone, address);
        members.put(memberId, member);

        System.out.println("New member created: " + name);

        return member;
    }

    private boolean validateMemberDetails(String name, String email,
            String phone, String address) {

        if (name == null || name.trim().length() < 2) {
            System.out.println("Invalid member name. Must be at least 2 characters.");
            return false;
        }

        if (email == null || !email.contains("@") || !email.contains(".")) {
            System.out.println("Invalid email format.");
            return false;
        }

        if (phone == null || phone.length() < 10) {
            System.out.println("Invalid phone number. Must be at least 10 digits.");
            return false;
        }

        if (address == null || address.trim().length() < 5) {
            System.out.println("Invalid address. Must be at least 5 characters.");
            return false;
        }

        return true;
    }

    private boolean canBorrow(Member member) {

        if (member.getBorrowedBooks().size() >= MAX_BORROWED_BOOKS) {
            System.out.println("Member has reached maximum borrowing limit of 5 books.");
            return false;
        }

        if (member.getTotalFines() > MAX_ALLOWED_FINES) {
            System.out.println("Member has outstanding fines exceeding $25.00. Cannot borrow.");
            return false;
        }

        return true;
    }

    private Book findOrCreateBook(String isbn, String title,
            String author, String category) {

        Book book = books.get(isbn);

        if (book != null) {
            return book;
        }

        if (title == null || author == null || category == null) {
            System.out.println("Book not found and insufficient information to create new book.");
            return null;
        }

        book = new Book(isbn, title, author, category);
        books.put(isbn, book);

        System.out.println("New book added to library: " + title);

        return book;
    }

    private void showBookAvailability(Book book) {

        System.out.println("Book is currently not available: " + book.getTitle());

        Date dueDate = book.getDueDate();

        if (dueDate != null) {
            System.out.println("Estimated return date: " + dateFormat.format(dueDate));

            long overdueDays = calculateOverdueDays(dueDate);

            if (overdueDays > 0) {
                double fine = calculateFine(overdueDays, book.getCategory());
                System.out.println("Book is overdue by " + overdueDays
                        + " days. Fine: $" + fine);
            }
        }
    }

    private Date calculateDueDate(int borrowDays) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, borrowDays);
        return calendar.getTime();
    }

    private void processBorrowing(Book book, Member member,
            String isbn, String memberId, Date dueDate) {

        book.setAvailable(false);
        book.setDueDate(dueDate);
        book.setBorrowerMemberId(memberId);

        member.addBorrowedBook(isbn);

        String transaction = "BORROW: Member " + memberId
                + " borrowed book " + isbn
                + " (" + book.getTitle() + ") on "
                + dateFormat.format(new Date())
                + " due " + dateFormat.format(dueDate);

        transactionHistory.add(transaction);
    }

    private void displayBorrowingConfirmation(Member member, Book book,
            String isbn, int borrowDays, Date dueDate) {

        System.out.println("=== BORROWING CONFIRMATION ===");
        System.out.println("Member: " + member.getName()
                + " (" + member.getMemberId() + ")");
        System.out.println("Book: " + book.getTitle()
                + " by " + book.getAuthor());
        System.out.println("ISBN: " + isbn);
        System.out.println("Category: " + book.getCategory());
        System.out.println("Borrow Date: " + dateFormat.format(new Date()));
        System.out.println("Due Date: " + dateFormat.format(dueDate));
        System.out.println("Borrowing Period: " + borrowDays + " days");
        System.out.println("Daily Fine Rate: $"
                + categoryFines.getOrDefault(book.getCategory(), 0.5));
        System.out.println("Books Currently Borrowed: "
                + member.getBorrowedBooks().size());
        System.out.println("Total Books Borrowed (Lifetime): "
                + member.getBorrowCount());
        System.out.println("==============================");
    }

    private long calculateOverdueDays(Date dueDate) {
        long difference = new Date().getTime() - dueDate.getTime();

        return difference > 0
                ? difference / MILLISECONDS_PER_DAY : 0;
    }

    private double calculateFine(long overdueDays, String category) {
        double rate = categoryFines.getOrDefault(category, 0.5);
        return overdueDays * rate;
    }

    public boolean returnBook(String isbn) {

        Book book = books.get(isbn);

        if (book == null) {
            System.out.println("Book not found: " + isbn);
            return false;
        }

        if (book.isAvailable()) {
            System.out.println("Book is not currently borrowed: " + book.getTitle());
            return false;
        }

        Member member = members.get(book.getBorrowerMemberId());

        if (member == null) {
            System.out.println("Borrowing member not found.");
            return false;
        }

        Date today = new Date();
        double fine = 0.0;

        if (book.getDueDate() != null) {
            long overdueDays = calculateOverdueDays(book.getDueDate());
            fine = calculateFine(overdueDays, book.getCategory());

            if (fine > 0) {
                member.setTotalFines(member.getTotalFines() + fine);
            }
        }

        book.setAvailable(true);
        book.setDueDate(null);
        member.removeBorrowedBook(isbn);
        book.setBorrowerMemberId(null);

        String transaction = "RETURN: Book " + isbn
                + " returned on " + dateFormat.format(today)
                + (fine > 0 ? " with fine $" + fine : "");

        transactionHistory.add(transaction);

        System.out.println("Book returned successfully"
                + (fine > 0 ? " with fine: $" + fine : ""));

        return true;
    }

    public void addBook(String isbn, String title,
            String author, String category) {

        if (books.containsKey(isbn)) {
            System.out.println("Book with ISBN " + isbn + " already exists.");
            return;
        }

        Book book = new Book(isbn, title, author, category);
        books.put(isbn, book);

        System.out.println("Book added successfully: " + title);
    }

    public void addMember(String memberId, String name, String email,
            String phone, String address) {

        if (members.containsKey(memberId)) {
            System.out.println("Member with ID " + memberId + " already exists.");
            return;
        }

        Member member = new Member(memberId, name, email, phone, address);
        members.put(memberId, member);

        System.out.println("Member added successfully: " + name);
    }

    public List<Book> searchBooks(String query) {

        List<Book> results = new ArrayList<>();

        for (Book book : books.values()) {
            if (book.getTitle().toLowerCase().contains(query.toLowerCase())
                    || book.getAuthor().toLowerCase().contains(query.toLowerCase())
                    || book.getIsbn().contains(query)) {
                results.add(book);
            }
        }

        return results;
    }

    public Member getMember(String memberId) {
        return members.get(memberId);
    }

    public Book getBook(String isbn) {
        return books.get(isbn);
    }

    public List<String> getTransactionHistory() {
        return new ArrayList<>(transactionHistory);
    }

    public int getTotalBooks() {
        return books.size();
    }

    public int getTotalMembers() {
        return members.size();
    }

    public int getAvailableBooks() {

        int count = 0;

        for (Book book : books.values()) {
            if (book.isAvailable()) {
                count++;
            }
        }

        return count;
    }
}