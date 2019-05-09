public class Student {
    private String id;
    private String gradeBookID;
    private String name;
    private String grade;

    public Student(String Name) {
        this.setId(helpers.GetHashID(Name, "0"));
        this.setName(Name);
    }

    public Student(String Name, String Grade) {
        this(Name);
        this.setGrade(Grade);
    }

    public Student(String Name, String Grade, Gradebook GradeBook) {

        this.setName(Name);
        this.setGrade(Grade);
        this.setGradeBookID(GradeBook.getID());
        this.setId(helpers.GetHashID(Name, GradeBook.getName()));
    }

    public String getId() {
        return id;
    }

    private void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGrade() {
        return grade;
    }

    public String getGradeBookID() {
        return gradeBookID;
    }

    public void setGradeBookID(String gradeBookID) {
        this.gradeBookID = gradeBookID;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getXML(){
        return "<student>\n" +
                "\t\t<grade>\n" +
                this.getGrade() +
                "\t\t</grade>\n" +
                "\t\t<name>\n" +
                this.getName() +
                "\t\t</name>\n" +
                "\t</student>";
    }
}
