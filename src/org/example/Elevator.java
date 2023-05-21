package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class Elevator implements Runnable {

    private final BlockingQueue<Request> requests;
    private Request currentRequest = new Request(0, ElevatorDirection.Up);
    private ElevatorDirection elevatorDirection = ElevatorDirection.Up;
    private int currentFloor = 0;
    private final int number;
    private boolean isFree = true;

    public Elevator(BlockingQueue<Request> requests, int number) {
        this.requests = requests;
        this.number = number;
    }

    @Override
    public void run() {
        System.out.println("Elevator " + this.number + " started to move from the " + this.currentFloor + " floor to the " + this.currentRequest.floor() + " floor");
        try {
            List<Integer> possiblePassingRequestsFloors = new ArrayList<>();
            while (this.currentFloor != this.currentRequest.floor()) {
                if (this.elevatorDirection == ElevatorDirection.Up) {
                    possiblePassingRequestsFloors.addAll(requests.stream().filter(request -> this.currentFloor < request.floor()
                            && this.currentRequest.floor() > request.floor() && this.elevatorDirection == this.currentRequest.elevatorDirection()).map(Request::floor).toList());
                } else {
                    possiblePassingRequestsFloors.addAll(requests.stream().filter(request -> this.currentFloor > request.floor()
                            && this.currentRequest.floor() < request.floor() && this.elevatorDirection == this.currentRequest.elevatorDirection()).map(Request::floor).toList());
                }
                if (possiblePassingRequestsFloors.contains(this.currentFloor)) {
                    System.out.println("Elevator " + this.number + " took passing request on the " + this.currentFloor + " floor");
                    this.requests.removeAll(this.requests.stream().filter(request -> request.floor() == this.currentFloor).toList());
                    possiblePassingRequestsFloors.removeAll(possiblePassingRequestsFloors.stream().filter(possiblePassingRequestsFloor -> possiblePassingRequestsFloor == this.currentFloor).toList());
                }
                System.out.println("Elevator " + this.number + " on the " + this.currentFloor + " floor now");
                this.currentFloor += this.elevatorDirection == ElevatorDirection.Up ? 1 : -1;
                Thread.sleep(1000);
            }
            System.out.println("Elevator " + this.number + " took passenger on the " + this.currentFloor + " floor");
            System.out.println("Elevator " + this.number + " is currently free");
            this.isFree = true;
            Thread.sleep(1000);
        } catch (InterruptedException exception) {
            System.out.println("Elevator running went wrong");
        }
    }

    public void setCurrentRequest(Request currentRequest) {
        this.currentRequest = currentRequest;
        this.elevatorDirection = this.currentFloor < this.currentRequest.floor() ? ElevatorDirection.Up : ElevatorDirection.Down;
    }

    public boolean isFree() {
        return this.isFree;
    }

    public void setIsFree(boolean isFree) {
        this.isFree = isFree;
    }

    public int getCurrentFloor() {
        return this.currentFloor;
    }
}