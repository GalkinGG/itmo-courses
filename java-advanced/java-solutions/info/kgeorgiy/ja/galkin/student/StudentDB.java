package info.kgeorgiy.ja.galkin.student;

import info.kgeorgiy.java.advanced.student.*;

import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StudentDB implements StudentQuery {

    private static final Function<Student, String>
            GET_FULL_NAME = student -> student.getFirstName() + " " + student.getLastName();

    private static final Comparator<Student> COMPARE_BY_NAME = Comparator.
            comparing(Student::getLastName).
            thenComparing(Student::getFirstName).
            thenComparing(Student::getId, Comparator.reverseOrder());


    private static final BinaryOperator<String> MERGE_FUNCTION = BinaryOperator.minBy(Comparator.naturalOrder());

    @Override
    public List<String> getFirstNames(List<Student> students) {
        return getStudents(students, Student::getFirstName);
    }

    @Override
    public List<String> getLastNames(List<Student> students) {
        return getStudents(students, Student::getLastName);
    }

    @Override
    public List<GroupName> getGroups(List<Student> students) {
        return getStudents(students, Student::getGroup);
    }

    @Override
    public List<String> getFullNames(List<Student> students) {
        return getStudents(students, GET_FULL_NAME);
    }

    @Override
    public Set<String> getDistinctFirstNames(List<Student> students) {
        // :NOTE: через collect
        // FIXED
        return students.stream().map(Student::getFirstName).collect(Collectors.toCollection(TreeSet::new));
    }

    @Override
    public String getMaxStudentFirstName(List<Student> students) {
        return students.stream().
                // :NOTE: naturalOrder
                // FIXED
                max(Comparator.naturalOrder()).
                map(Student::getFirstName).orElse("");
    }

    @Override
    public List<Student> sortStudentsById(Collection<Student> students) {
        // :NOTE: naturalOrder
        // FIXED
        return sortStreamToList(students.stream(), Comparator.naturalOrder());
    }

    @Override
    public List<Student> sortStudentsByName(Collection<Student> students) {
        return sortStreamToList(students.stream(), COMPARE_BY_NAME);
    }

    @Override
    public List<Student> findStudentsByFirstName(Collection<Student> students, String name) {
        return findStudentsByParam(students, Student::getFirstName, name);
    }

    @Override
    public List<Student> findStudentsByLastName(Collection<Student> students, String name) {
        return findStudentsByParam(students, Student::getLastName, name);
    }

    @Override
    public List<Student> findStudentsByGroup(Collection<Student> students, GroupName group) {
        return findStudentsByParam(students, Student::getGroup, group);
    }

    @Override
    public Map<String, String> findStudentNamesByGroup(Collection<Student> students, GroupName group) {
        return findStudentsByGroup(students, group).stream()
                .collect(Collectors.toMap(Student::getLastName, Student::getFirstName, MERGE_FUNCTION));
    }

    private <T> List<T> getStudents(List<Student> students, Function<Student, T> getter) {
        return students.stream()
                .map(getter)
                .collect(Collectors.toList());
    }

    private List<Student> sortStreamToList(Stream<Student> students, Comparator<Student> comparator) {
        return students.sorted(comparator).collect(Collectors.toList());
    }

    private <T> List<Student> findStudentsByParam(Collection<Student> students, Function<Student, T> getter, T param) {
        return sortStreamToList(
                students.stream().
                        filter(student -> getter.apply(student).equals(param)), COMPARE_BY_NAME
        );
    }
}
