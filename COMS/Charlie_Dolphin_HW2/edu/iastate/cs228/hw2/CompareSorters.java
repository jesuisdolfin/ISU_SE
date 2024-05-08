package edu.iastate.cs228.hw2;

/**
 *  
 * @author Charlie Dolphin
 *
 */

/**
 * 
 * This class executes four sorting algorithms: selection sort, insertion sort, mergesort, and
 * quicksort, over randomly generated integers as well integers from a file input. It compares the 
 * execution times of these algorithms on the same input. 
 *
 */

import java.io.FileNotFoundException;
import java.util.Scanner; 
import java.util.Random; 


public class CompareSorters {
	/**
	 * Repeatedly take integer sequences either randomly generated or read from files. 
	 * Use them as coordinates to construct points.  Scan these points with respect to their 
	 * median coordinate point four times, each time using a different sorting algorithm.  
	 * 
	 * @param args
	 **/
	public static void main(String[] args) throws FileNotFoundException {		
		// 
		// Conducts multiple rounds of comparison of four sorting algorithms.  Within each round, 
		// set up scanning as follows: 
		// 
		//    a) If asked to scan random points, calls generateRandomPoints() to initialize an array 
		//       of random points. 
		// 
		//    b) Reassigns to the array scanners[] (declared below) the references to four new 
		//       PointScanner objects, which are created using four different values  
		//       of the Algorithm type:  SelectionSort, InsertionSort, MergeSort and QuickSort. 
		// 
		// 	

		PointScanner[] scanners = new PointScanner[4]; 

		System.out.println("(1) Random Integers \n (2) From File");
		Scanner s = new Scanner(System.in);
		int userChoice = s.nextInt();
		
		if (userChoice == 1) {
			System.out.print("Enter number of random points: ");
			int numPoints = s.nextInt();
			Point[] points = generateRandomPoints(numPoints, new Random());
			scanners[0] = new PointScanner(points, Algorithm.SelectionSort);
			scanners[1] = new PointScanner(points, Algorithm.InsertionSort);
			scanners[2] = new PointScanner(points, Algorithm.MergeSort);
			scanners[3] = new PointScanner(points, Algorithm.QuickSort);
		}
		else if (userChoice == 2) {
			System.out.print("Enter file name: ");
			s.next();
			String file = s.nextLine();
			scanners[0] = new PointScanner(file, Algorithm.SelectionSort);
			scanners[1] = new PointScanner(file, Algorithm.InsertionSort);
			scanners[2] = new PointScanner(file, Algorithm.MergeSort);
			scanners[3] = new PointScanner(file, Algorithm.QuickSort);
		}

		for (PointScanner ps : scanners) {
			ps.scan();
			System.out.println(ps.stats() + '\n');
		}

		s.close();
	}
	
	
	/**
	 * This method generates a given number of random points.
	 * The coordinates of these points are pseudo-random numbers within the range 
	 * [-50,50] � [-50,50]. Please refer to Section 3 on how such points can be generated.
	 * 
	 * Ought to be private. Made public for testing. 
	 * 
	 * @param numPts  	number of points
	 * @param rand      Random object to allow seeding of the random number generator
	 * @throws IllegalArgumentException if numPts < 1
	 */
	public static Point[] generateRandomPoints(int numPts, Random rand) throws IllegalArgumentException { 
		if (numPts < 1) {
			throw new IllegalArgumentException("Needs at least 1 point");
		}
		Point[] randPoints = new Point[numPts];
		for (int i = 0; i < numPts; i++) {
			int x = rand.nextInt(101) - 50;
			int y = rand.nextInt(101) - 50;
			randPoints[i] = new Point(x, y);
		}
		return randPoints;
	}
	
}
