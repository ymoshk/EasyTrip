package schedule.node;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import schedule.DaySchedule;

import java.time.LocalTime;

class ScheduleNodeTest {

    ScheduleNode node = new ScheduleNode(LocalTime.now(), LocalTime.now().plusHours(3), new DaySchedule(), null);

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getId() {
        System.out.println("get id test");
        System.out.println(node.getId());
    }

    @Test
    void addToStart() throws IllegalAccessException {
        print();
        this.node.addToStart(10);
        print();
    }

    @Test
    void reduceFromStart() throws IllegalAccessException {
        print();
        this.node.reduceFromStart(10);
        print();
    }

    @Test
    void addToEnd() throws IllegalAccessException {
        print();
        this.node.addToEnd(10);
        print();
    }

    @Test
    void reduceFromEnd() throws IllegalAccessException {
        print();
        this.node.reduceFromEnd(10);
        print();
    }

    @Test
    void setStartTime() throws IllegalAccessException {
        print();
        this.node.setStartTime(LocalTime.now().minusHours(3));
        print();
    }

    @Test
    void setEndTime() throws IllegalAccessException {
        print();
        this.node.setStartTime(LocalTime.now().plusHours(3));
        print();
    }

    @Test
    void split() throws IllegalAccessException {
        System.out.println("Before:");
        node.print();
        System.out.println("After:");
        ScheduleNode copy = node.split();
        System.out.println("Real:");
        this.node.print();
        System.out.println("Copy:");
        copy.print();
    }

    @Test
    void testSplit() throws IllegalAccessException {
        System.out.println("Before:");
        node.print();
        System.out.println("After:");
        ScheduleNode copy = node.split(179);
        System.out.println("Real:");
        this.node.print();
        System.out.println("Copy:");
        copy.print();
    }

    @Test
    void testSplit1() throws IllegalAccessException {
        System.out.println("Before:");
        node.print();
        System.out.println("After:");
        ScheduleNode copy = node.split(LocalTime.now().plusMinutes(30));
        System.out.println("Real:");
        this.node.print();
        System.out.println("Copy:");
        copy.print();
    }

    @Test
    void testClone() {
        System.out.println("original:");
        this.node.print();
        System.out.println("cloned:");
        this.node.clone().print();
    }

    @Test
    void print() {
        this.node.print();
    }

    @Test
    void subtractTimes() {
        System.out.println(ScheduleNode.subtractTimesToMinutes(LocalTime.now(), LocalTime.now().plusHours(1).plusMinutes(5)));
    }

    @Test
    void splitAndAddMiddleNode() throws IllegalAccessException {
        ScheduleNode middle = new ScheduleNode(LocalTime.now().plusHours(1),
                LocalTime.now().plusMinutes(90), new DaySchedule(), null);

        System.out.println("Before:");
        node.print();
        System.out.println("After:");
        ScheduleNode copy = node.splitAndAddMiddleNode(middle);
        System.out.println("First:");
        this.node.print();
        System.out.println("Second:");
        middle.print();
        System.out.println("Third:");
        copy.print();
    }
}