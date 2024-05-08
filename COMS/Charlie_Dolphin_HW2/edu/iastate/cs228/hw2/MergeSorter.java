package edu.iastate.cs228.hw2;

import java.io.FileNotFoundException;
import java.lang.NumberFormatException; 
import java.lang.IllegalArgumentException; 
import java.util.InputMismatchException;

/**
 *  
 * @author Charlie Dolphin
 *
 */

/**
 * 
 * This class implements the mergesort algorithm.   
 *
 */

public class MergeSorter extends AbstractSorter
{
	// Other private instance variables if needed
	
	/** 
	 * Constructor takes an array of points.  It invokes the superclass constructor, and also 
	 * set the instance variables algorithm in the superclass.
	 *  
	 * @param pts   input array of integers
	 */
	public MergeSorter(Point[] pts) 
	{
		super(pts);
		super.algorithm = "mergesort";
	}


	/**
	 * Perform mergesort on the array points[] of the parent class AbstractSorter. 
	 * 
	 */
	@Override 
	public void sort()
	{
		mergeSortRec(points);

	}
	/**
	 * This is a recursive method that carries out mergesort on an array pts[] of points. One 
	 * way is to make copies of the two halves of pts[], recursively call mergeSort on them, 
	 * and merge the two sorted subarrays into pts[].   
	 * 
	 * @param pts	point array 
	 */
	private void mergeSortRec(Point[] pts)
	{
		if (pts.length <= 1) {
			return;
		}

		Point[] left = new Point[pts.length / 2];
		Point[] right = new Point[pts.length - (pts.length / 2)];

		for (int i = 0; i < left.length; i++) {
			left[i] = pts[i];
		}
		for (int j = 0; j < right.length; j++) {
			right[j] = pts[left.length + j];
		}
		mergeSortRec(left);
		mergeSortRec(right);
		merge(pts, left, right);
	}

	private void merge(Point[] pts, Point[] left, Point[] right) {
		int i = 0;
		int j = 0;
		int index = 0;

		while ((i < left.length) && (j < right.length)) {
			if (pointComparator.compare(left[i], right[j]) < 0) {
				pts[index] = left[i++];
			}
			else {
				pts[index] = right[j++];
			}
			index++;
		}

		while (i < left.length) {
			pts[index++] = left[i++];
		}
		while (j < right.length) {
			pts[index++] = right[j++];
		}
	}

}
