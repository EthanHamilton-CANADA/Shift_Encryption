package no_edits;

import java.awt.Color;

import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

//import no_edits.PathResolver;

public class Encrypt {
	
	//The below variable is currently unused, and only previously used for testing.
	private static int[][] TEST_CASE = {{1,0,0,0,0,0,0,1},{0,1,0,0,0,0,1,0},{0,0,1,0,0,1,0,0},{0,0,0,1,1,0,0,0},
										{0,0,0,1,1,0,0,0},{0,0,1,0,0,1,0,0},{0,1,0,0,0,0,1,0},{1,0,0,0,0,0,0,1},{1,0,0,0,0,0,0,1}};	
	
	//The random variable that will be used to generate decryption keys later.
	static Random rnd = new Random();
	
	private static int x = 0;   //variable that will be used to temporarily store the individual integers/keys for moving the rows x positions to the left or right in the array.
	private static int y = 0;	//variables that will be used to temporarily store the individual integers/keys for moving the columns y positions up or down in the array. 
	
	private static int[] holderX = {1,3,4,7,6,4,2,1}; //the static one-dimensional integer array holderX, which will store all the keys to move each row x positions left or right in an image.  Ignore the initialization, it is overwritten later and is a remnant of testing.
	private static int[] holderY = {1,3,4,7,6,4,2,1,5};  //the static one-dimensional integer array holderY, which will store all the keys to move each column y positions up or down in an image.  Ignore the initialization, it is overwritten later and is a remnant of testing.
	
	private static BufferedImage[] bf1;  //a one-dimensional BufferedImage array of all the images to be encrypted.
	
	private static int[] width; //a one-dimensional width array used to store the width of all the images that are to be encrypted.
	private static int[] height; //a one-dimensional height array used to store the height of all the images that are to be encrypted.
	
	private static int[][] data; //a two-dimensional private integer array that stores all the red values of an image.
	private static int[][] data1; //a two-dimensional private integer array that stores all the green values of an image.
	private static int[][] data2; //a two-dimensional private integer array that stores all the blue values of an image.
	
	private static List<String> subPathsList; //Will eventually give us the strings for all the images contained within the resources folder.
	
	private static String contentType;  //String variable contentType is declared.  Will eventually store the file type.  E.g., .png, .jpg, .jpeg, etc.
	private static File sourceFile;  //File object sourceFile is declared.  Will temporarily store all the files that are to be encrypted.
	private static boolean delete = false;  //a variable to delete the original copies.
	private static String fileNameWithOutExt; //A string variable that will eventually be used to store the names of the files without their type extension.
	
	public static void main(String[] args) {  //main method
		getFileNames(); //calls the getFileNames() method.
		InitializeImages(); //calls the InitializeImages() method.
	}
	
	
	/**
	 *SHIFT_LEFT
	 * @param array
	 * @param row
	 * @param n
	 * @param shift
	 *
	 * TEST_CASE for SHIFT_LEFT
	 *	1	0	1	1	1
	 * 	0	1	1	0	0
	 * 	1	0	1	0	0 <-- We'll take this row as our example.
	 * 	0	1	0	0	0
	 *  0	0	1	1	1
	 *  
	 *  The parameters for our ShiftLeft program as follows:
	 *  	A two dimensional integer array named 'array' that stores our pixel data.
	 *  	An integer variable called 'row' specifying the row that is to be shifted/ translated.
	 *  	An integer variable named 'n' that stores the length of the row that is to shifted/ translated.
	 *  	An integer variable called 'shift' that stores how many positions left the row is to be shifted/translated.
	 *  
	 *  Therefore, assuming our input into the row is as follows:
	 *  	ShiftLeft(ImagePixelData, 2, 5, 2)
	 *  
	 *  We have then taken our above 5x5 two-dimensional binary array, selected the row at index 2 (the third/ middle row, 
	 *  because of 0-based indexing, i.e., 0,1,2), have specified the length of the row to be 5, and have stated we would
	 *  like to shift/translate the row 2 spots left.
	 *  
	 *  E.g., This is what we currently have
	 * 	1	0	1	0	0  <-- This has 5 elements and we'd like to shift/translate it 2 positions to the left. 
	 * 
	 * 	Now....
	 * 	Should 'n' be less than one, we return as the image doesn't have any width.
	 *  This is done via the statement: "if (n<1) return;"
	 *  
	 *  Then, the shift integer variable is set equal to itself undergoing a modulus operation with n "shift = shift % n"
	 *  The modulus operation returns the remainder of a division operation.  Thus, the integer variable shift will 
	 *  become equal to the remaining space left in the row that does not evenly fit into the length of the row.  
	 *  E.g., in our example, we have the following:
	 * 	1	0	1	0	0  <-- where n = 5, and shift = 2
	 * 	Therefore, we have an operation- what is the remainder of 2/5?  Well, 2 fits into 5 zero times with a remainder
	 * 	of two.  Therefore, this example will return shift = 2.  Which we can see is correct- as if we are shifting the
	 *  current row to the left two positions we will need to hold the left two elements of the current row in a 
	 *  temporary array and append them to the right side of the current row at a later time when all the other elements
	 *  in the row have been shifted two positions to the left.  Furthermore, if we face the condition that the modulus
	 *  is 0, then shift is either 0 or the same length of this row, and so the resultant row will be the same either way
	 *  and so we return.  "if (shift == 0) return;"  
	 *  
	 *  Next, we create a new one-dimensional integer array called 'tmp' and set it to a size equal to the number of 
	 *  positions on the left side of the row that we need it to store.  "int[] tmp = new int[shift];"  E.g., in our 
	 *  example, that will then be a size of two.
	 *  
	 *  After that, we iterate from left to right over these positions in the left side of our array that we need to 
	 *  store, "for(int i = 0; i < shift; i++){" and set the values within our one-dimensional integer 'tmp' array to 
	 *  these values also in a left-to-right fashion, and store these values in this array for later use once the current
	 *  row of the array has been shifted/ transformed to the left. "tmp[i] = array[i][row];"  E.g., in our example we
	 *  will have the following:
	 *  1	0	1	0	0  <--This is the current row of the array, and shift = 2.  Therefore, the iteration will go from
	 *  i = 0 to i = 1, and so tmp[0] = 1, and tmp[1] = 0  
	 *  
	 *  Next, we will iterate from 0 to the length of the array minus the length of data already stored in the tmp array.
	 *  "for (int i = 0; i < n - shift; i++) {"  Then, we will set the array at the current row "[row]", and at the 
	 *  current column "[i]"- (the current column being determined by 'i') equal to the array at the current column (as 
	 *  determined by "i + shift", an expression that gets you the first integer (at i=0) that hasn't been put into the
	 *  tmp array plus every value to the right as 'i' increases) and at the current row "[row]".  
	 *  E.g., in our example, n = 5, and shift is now/ and always was equal to 2.  Therefore, 'i' will iterate from 0
	 *  to 2 (inclusive) as n-shift = 5-2 = 3.  This will move all the values to the right of the last value put into 
	 *  tmp two positions to the left.  
	 *  at i = 0
	 *  array[0][row] = array[0+2][row] = array[2][row] = 1
	 *  at i = 1
	 *  array[1][row] = array[1+2][row] = array[3][row] = 0
	 *  at i = 2
	 *  array[2][row] = array[2+2][row] = array[4][row] = 0   
	 *  As we can see, this grabs all the elements to the right of the rightmost element that was put into tmp.
	 *  
	 *  Then, we iterate from 0 to shift, aka from 0 to 1 (inclusive) in this example. "for (int i = 0; i < shift; i++) {"
	 *  This will aid us in appending the values in the tmp array to the rightmost side of the permanent array.
	 *  E.g., at i = 0
	 *  array[n-shift+i][row] = array[5-2+0] = array[3] = tmp[i] = tmp[0] = 1
	 *  at i = 1
	 *  array[n-shift+i][row] = array[5-2+1] = array[4] = tmp[i] = tmp[1] = 0
	 *  
	 *  Therefore, our final array at the current row is as follows:
	 *  1	0	0	1	0
	 *  Which, is the result of shifting/ translating all the elements in 1 0 1 0 0 two positions left.  Yay!
	 *  
	 *  Tl;dr
	 *  1 0 1 0 0 at a set row, n = 5, and shift = 2
	 *  tmp[0] = 1, tmp[1] = 0  //stores the two leftmost bits		//tmp[i] = array[i][row]; does this
	 *  array[0][row] = 1, array[1][row] = 0, array[2][row] = 0		//array[i][row] = array[i + shift][row]; does this
	 *  array[3][row] = tmp[0] = 1, array[4][row] = tmp[1] = 0.		//array[n - shift + i][row] = tmp[i]; does this
	 *  Thus giving the values at the current row in array to be:
	 *  1	0	0	1	0 
	 */
	
	
	
	public static void ShiftLeft(int[][] array, int row, int n, int shift) { 
	    if (n < 1) return;
	    
	    shift = shift % n;
	    if (shift == 0) return;
	    
	    int[] tmp = new int[shift];

	    for (int i = 0; i < shift; i++) {
	        tmp[i] = array[i][row];
	    }

	    for (int i = 0; i < n - shift; i++) {
	        array[i][row] = array[i + shift][row];
	    }

	    for (int i = 0; i < shift; i++) {
	        array[n - shift + i][row] = tmp[i];
	    }
	}
	
	
	/**
	 * SHIFT_UP
	 * @param array
	 * @param column
	 * @param n
	 * @param shift
	 * 
	 * TEST CASE FOR ShiftUp
	 *  1	0	1	1	1
	 * 	0	1	1	0	0
	 * 	1	0	1	0	0 
	 * 	0	1	0	0	0
	 *  0	0	1	1	1
	 *  ^
	 * 	|
 	 * 	|  The column being pointed to is the column we will use.
 	 *  so, column = 0, n = 5, and we will set shift = 2
 	 * 
 	 * 	The two-dimensional integer array parameter in the method constructor holds the pixel data
 	 *  the integer variable 'column' will specify the column in the two dimensional array that is to be shifted up.
 	 *  integer variable n will return the number of elements in the column
 	 *  integer variable shift will specifiy how many positions up the column will be shifted.
 	 *  
 	 *  if (n < 1) return;  This line of code will return the method if the number of elements in the column 
 	 *  is 0, aka, if the image doesn't have any height (an erroneous condition).  For our example, this will not trigger.
 	 *  
 	 *  shift = shift % n; this line of code will return the modulus of shift and n and put it into the shift int variable.
 	 *  Given that 2 fits evenly into 5 zero times with a remainder of two, shift will now become two.  This gives us
 	 *  the number of positions in the original array that we'll need to save to a temporary arrary to preserve
 	 *  the values that would otherwise be lost by shifting the original array.
 	 *   
 	 *  if (shift == 0) return; then this would indicate that the modulus would return 0, and so the image shifting up 
 	 *  would be shifted 0 positions, or by a number equivalent to the height of the array/ image.  Aka, the image/ array
 	 *  will otherwise be the same, and so we return.
 	 *  
 	 *  int[] tmp = new int[shift]; This line of code creates a one-dimensional integer array called tmp of the size determined 
 	 *  by the shift integer variable which is equal to number of positions that would otherwise be displaced/ overwritten in the array.
 	 *  
 	 *  for (int i = 0; i < shift; i++) {
	 *       tmp[i] = array[column][i];
	 *   }  
	 *   The above code iterates from 0 to i < shift (which in our case means i=0 and i=1).  This iterative loop will 
	 *   go through the top values in the column that would otherwise be overwritten by shifting the values in the array
	 *   by shift positions up.
	 *   Given that in our example, shift = 2, this loop will iterate from i=0 to i=1, and will store the top two values
	 *   in array[column][i] to the tmp[i] array.
	 *   Here is the column in our example array that we will be going over for shift = 2.   
	 *   1  <---	tmp[0] = 1
	 *   0	<---	tmp[1] = 0
	 *   1
	 *   0
	 *   0
	 *   therefore, in our example tmp[0] = 1, and tmp[1] = 0
	 *   
	 *   for (int i = 0; i < n - shift; i++) {
	 *       array[column][i] = array[column][i + shift];
	 *   } 
	 *   The above code will go from 0 to i < n-shift.  Aka, 'i' will iterate from 0 to one less than (column_height-shift).  Which, in our 
	 *   example, will therefore go from 0 to (5-2)-1 = 0 to 2.  Therefore, i = 0,1, and 2.
	 *   Therefore, we will get the following across our 'i' iterations of the code 
	 *   "array[column][i] = array[column][i + shift]":
	 *   at i=0
	 *   array[column][0] = array[column][0+2] = array[column][2] = 1
	 *   at i=1
	 *   array[column][1] = array[column][1+2] = array[column][3] = 0
	 *   at i=2
	 *   array[column][2] = array[column][2+2] = array[column][4] = 0
	 *   so, array[column][0] = 1, array[column][1] = 0, and array[column][2] = 0
	 *   Which means that all values in the array have now been shifted up two positions in our example.  Although, 
	 *   it's important to note that the top two positions (that have now been overwritten and whom were previously 
	 *   stoed inside the tmp[] array) still need to be written into the bottom of the column in the array[][] variable.
	 *   
	 *   Finally, for the following code:
	 *   for (int i = 0; i < shift; i++) {
	 *       array[column][n - shift + i] = tmp[i];
	 *   }This code, yet again goes from 0 to one less than shift.  I.e., i=0, and i=1 in our example. 
	 *   Furthermore, the code "array[column][n-shift+i] = tmp[i]" will put the value of tmp[i] into the arrays current
	 *   column at row [column_height - shift + i].  E.g., for i = 0, the first / topmost value of tmp[i] should be put
	 *   into position array[column][3] as the length of the arrays column is 5, and so given 0-based indexing  
	 *   the fourth position in the array (i=3).  so, array[column][3] needs to be 1, and array[column][4] needs to be 0.
	 *   
	 *   Going over it, "array[column][n - shift + i] = tmp[i];" we get:
	 *   at i = 0
	 *   array[column][5-2+0] = array[column][3] = tmp[0] = 1
	 *   at i = 1
	 *   array[column][5-2+1] = array[column][4] = tmp[1] = 0
	 *   
	 *   Therefore, from this entire example, we will get the following:
	 *   array[column][0] = 1
	 *   array[column][1] = 0
	 *   array[column][2] = 0
	 *   array[column][3] = 1
	 *   array[column][4] = 0
	 *   So... our final result is the following:
	 *   
	 *   1
	 *   0
	 *   0
	 *   1
	 *   0
	 *   
	 *   Which we can see, is the result of shifting the original column up two positions.  Yay!
	 */
	
	
	public static void ShiftUp(int[][] array, int column, int n, int shift) {
	    if (n < 1) return;
	    
	    shift = shift % n;
	    if (shift == 0) return;
	    
	    int[] tmp = new int[shift];

	    for (int i = 0; i < shift; i++) {
	        tmp[i] = array[column][i];
	    }

	    for (int i = 0; i < n - shift; i++) {
	        array[column][i] = array[column][i + shift];
	    }

	    for (int i = 0; i < shift; i++) {
	        array[column][n - shift + i] = tmp[i];
	    }
	}
	
	
	/**
	 * SHIFT_DOWN
	 * @param array
	 * @param column
	 * @param n
	 * @param shift
	 * 
	 *  TEST_CASE for ShiftDown
	 *	1	0	1	1	1
	 * 	0	1	1	0	0
	 * 	1	0	1	0	0
	 * 	0	1	0	0	0
	 *  0	0	1	1	1
	 *  ^
	 *  |
	 *  |
	 *  We'll take this column again, but shift it down.  E.g., column = 0, n = 5, and shift = 2. Aka,
	 *  we'll take the first column in the array (index 0), of column length 5, and shift it 2 positions down.
	 *  
	 *  "if(n < 1) return;" would exit the method should the column length be less than one- an erroneous condition.
	 *  
	 *  "shift = shift % n;" shift variable is set equal to itself undergoing a modulus operation with n.  Given that shift=2 and n=5, 
	 *  a modulus will return the remainder of a division operation.  And so given that 2 fits into 5 zero times, but with
	 *  a remainder of two, shift will now be set equal to 2 again.  This ensures that the shift variable is always the size
	 *  needed to store the information in the column that would otherwise be overwritten and lost on the shift of the column
	 *  down.
	 *  
	 *  "int[] tmp = new int[shift];" one-dimensional integer variable tmp is set equal to a size necessary to accommodate the values
	 *  that would otherwise be overwritten and lost in the original array by the shift down.
	 *  
	 *  "for(int i = 0; i < shift; i++) { 
	 *		tmp[i] = array[column][n - shift + i]; 
	 *	}" The above loop will iterate from 0 to 1 less than shift.  For our example, i=0,1.  This ensures that only the 
	 *	necessary values are put into the tmp[] array.  
	 *	Furthermore, given that this column is of a height of 5 values, and we expect to shift the column down two values
	 *	in our example, we therefore need to write the two bottom-most values of this array into the tmp array- as they 
	 *	would be the ones that would otherwise be overwritten and lost if we did not.  Therefore, for our example, we 
	 *	expect the following of the line of code "tmp[i] = array[column][n - shift + i];" to do...
	 *	at i = 0
	 *	tmp[0] = array[column][5-2+0] = array[column][3] = 0
	 *	at i = 1
	 *	tmp[1] = array[column][5-2+1] = array[column][4] = 0
	 *	As we can see, this stores the two bottom-most values of our array into tmp.
	 *	
	 *	
	 *	"for (int i = n - 1; i >= shift; i--) { 
	 *       array[column][i] = array[column][i-shift]; 
	 *   }" Next, we need to shift all the values in the array down by shift positions.
	 *	This can be accomplished by iterating over from the length of the column - 1 (due to 0-based indexing) to a value
	 *	greater than or equal to the shift value.  This will ensure that only the values from the bottom of the column to the top-most
	 *	position of the column that wasn't put into the tmp array are moved.  E.g., for our example, given n=5, and shift=2:
	 *	at i = 4
	 *	array[column][4] = array[column][4-2] = array[column][2] = 1
	 *	at i = 3
	 *	array[column][3] = array[column][3-2] = array[column][1] = 0
	 *	at i = 2
	 *	array[column][2] = array[column][2-2] = array[column][0] = 1
	 *	Now, all of the values that were not put into the tmp array have been shifted the proper positions down.  In our
	 *	example, that would be two positions down.
	 *	
	 *	Finally, we need to put the values that we put into tmp back into the array in the column.  Specifically, 
	 *	in our example, in the order so that the first, aka topmost element put into the tmp array 
	 *	(the value originally in the fourth position of the array (index = 3) needs to be put at index 1 of the 
	 *	current column in the shifted array.  This can be accomplished by the following:
	 *	iterating from i=0 to i<shift.  then, we just set array[column][i] = tmp[i].
	 *	Hence the code, 
	 *	"for(int i = 0; i < shift; i++) { 
	 *		array[column][i] = tmp[i];
	 *	}"
	 *	Thus, 
	 *	array[column][0] = 0
	 *	array[column][1] = 0
	 *	array[column][2] = 1
	 *	array[column][3] = 0
	 *	array[column][4] = 1
	 *	
	 *	Which is the column shifted down two positions.  Yay!
	 */
	
	
	public static void ShiftDown(int[][] array, int column, int n, int shift) { //4
		if (n < 1) return; 
		
		shift = shift % n;
		if (shift == 0) return;
		
		int[] tmp = new int[shift]; 
		
		for(int i = 0; i < shift; i++) { 
			tmp[i] = array[column][n - shift + i]; 
		}
	    for (int i = n - 1; i >= shift; i--) { 
	        array[column][i] = array[column][i-shift]; 
	    }
	    for(int i = 0; i < shift; i++) { 
			array[column][i] = tmp[i];
		}
	}
	
	
	/**
	 * SHIFT_RIGHT
	 * @param array
	 * @param column
	 * @param n
	 * @param shift
	 * 
	 *  TEST_CASE for ShiftRight
	 *	1	0	1	1	1
	 * 	0	1	1	0	0
	 * 	1	0	1	0	0 <-- We'll use this row, row = 2, n = 5, shift = 2.
	 * 	0	1	0	0	0
	 *  0	0	1	1	1
	 *  
	 *  Where row=2 given 0-based indexing, the length of the row is 5, hence n=5, and we're shifting the row two
	 *  positions to the right, hence shift = 2.
	 *  
	 *  "if (n < 1) return;" catches erroneous conditions where the length of the row is 0, and returns.
	 *  
	 *   "shift = shift % n;" returns the modulus of shift and n and puts it into the shift integer.  This ensures
	 *   that shift is now equal to the number of positions that need to be stored when shifting right so that the 
	 *   data of those last positions on the right side of the row are not lost when being overwritten, and are instead
	 *   put into a temporary array to then later be put back into the main array row after all the other values in the 
	 *   array row are shifted right.
	 *   
	 *   "if (shift == 0) return;" If the randomly generated shift variable is 0 or equal to the image width/ row width,
	 *   then that means the resultant row would be equal to the original, and hence this function doesn't need to be used
	 *   and so is returned.
	 *   
	 *   "int[] tmp = new int[shift];" declares a new temporary one-dimensional integer array called 'tmp' and sets it 
	 *   equal to the exact size needed to store the values that are going to be overwritten in the original array.  This
	 *   is so that these values can be written into the original array after that original array is translated right
	 *   so that the values that would otherwise be overwritten are not lost and put back into the array.
	 *   
	 *  "for(int i = 0; i < shift; i++) {  
	 *		tmp[i] = array[(n - 1)-i][row]; 
	 *	}" This code iterates from 0 to one less than shift.  E.g., in our instance i = 0, and 1.  Then,
	 *  tmp[0] is set equal to the rightmost value in the row, and tmp[1] is set equal to the value left of that.
	 *   
	 *  thus, in our example, tmp[0] = 0, tmp[1] = 0 
	 *  
	 *  "for (int i = n - 1; i > 0+(shift-1); i--) { 
	        array[i][row] = array[i - shift][row]; 
	    }"  This code will iterate from the width of the row (0-based indexing) to shift-1 (0-based indexing).  E.g.,
	    in our example, this will iterate from 4 to 2.  i = 4, 3, and 2.  Then the array at the current 'i' position
	    in the array will be set equal to the value two positions left.
	    E.g., in our example for the following line of code: "array[i][row] = array[i - shift][row];"
	    at i = 4  
	    array[4][row] = array[4-2][row] = 1
	    at i = 3
	    array[3][row] = array[3-2][row] = 0
	    at i = 2 
	    array[2][row] = array[0][row] = 1
	    
	    Finally, the stored values in tmp are put back into the array.
	    "for(int i = shift-1; i >= 0; i--) { 
			array[i][row] = tmp[i]; 
		}"  puts the values from tmp back into their proper position in the original translated array.
	*/

	public static void ShiftRight(int[][] array, int row, int n, int shift) {
		if (n < 1) return; 
		
		shift = shift % n; 
		if (shift == 0) return; 
		
		int[] tmp = new int[shift]; 
		
		for(int i = 0; i < shift; i++) {  
			tmp[i] = array[(n - 1)-i][row]; 
		}
	    for (int i = n - 1; i > 0+(shift-1); i--) { 
	        array[i][row] = array[i - shift][row]; 
	    }
	    for(int i = shift-1; i >= 0; i--) { 
			array[i][row] = tmp[i]; 
		}
	}
	
	/**
	 * Initialize the image that is to be encrypted
	 */
public static void InitializeImages() {
	
		bf1 = new BufferedImage[subPathsList.size()];  //generates an array of type BufferedImage equivalent to the number of target files in the subPathsList ArrayList.  Stores each individual image.
		width = new int[subPathsList.size()];  //generates an integer array equivalent to the number of target files in the subPathsList ArrayList.  Stores the width of each individual image.
		height = new int[subPathsList.size()];  //generates an integer array equivalent to the number of target files in the subPathsList ArrayList  Stores the height of each individual image.
		for(int k = 0; k < subPathsList.size(); k++) {  //k is incremented for every path present in the subPathsList ArrayList.
			bf1[k] = loadImage(subPathsList.get(k).trim()); //BufferedImage array bf1 at index k is set equal to the BufferedImage specified by the k'th element in the subPathsList. 
			
			Path p = Paths.get(subPathsList.get(k).trim()); //All this block of code does is return the name of the file without its extension.
			fileNameWithOutExt = p.getFileName().toString(); //sets the filename equal to the filename.
			fileNameWithOutExt = fileNameWithOutExt.replaceFirst("[.][^.]+$", ""); //removes the file extension
			System.out.println(fileNameWithOutExt);//prints the name of the file without the extension.
			
			deleteExtraneousFile(); //calls the deleteExtraneousFile() method.
			width[k] = bf1[k].getWidth(); //integer array width at increment k is set equal to the width of bf1's kth image.
			height[k] = bf1[k].getHeight(); //integer array height at increment k is set equal to the height of bf1's kth image.
			data = new int[width[k]][height[k]];  //a new data array of type int is declared and given identical dimensions to the current k'th image.
			data1 = new int[width[k]][height[k]]; //a new data1 array of type int is declared and given identical dimensions to the current k'th image.
			data2 = new int[width[k]][height[k]]; //a new data2 array of type int is declared and given identical dimensions to the current k'th image.
			for(int j = 0; j < height[k]; j++) {  //iterates through the rows of an image pixel by pixel.
				for(int i = 0; i < width[k]; i++) { //iterates through the columns of an image pixel by pixel.
					int rgb = bf1[k].getRGB(i, j); //integer rgb is initialized and set equal to the current RGB value of the current images current pixel.
					Color color = new Color(rgb); //A new color variable of type Color is initialized and given the same value as the rgb variable above.
					int red = color.getRed(); //integer red is set equal to the color variables getRed integer value.
					data[i][j] = red; //the red integer value is put into the data array at the proper i,j location (equivalent to the location of that same red value in the current image).
					
					int green = color.getGreen();  //integer green is set equal to the color variables getGreen integer value.
					data1[i][j] = green; //the green integer value is put into the data1 array at the proper i,j location (equivalent to the location of that same green value in the current image).
					
					int blue = color.getBlue();  //integer blue is set equal to the color variables getBlue integer value.
					data2[i][j] = blue; //the blue integer value is put into the data2 array at the proper i,j location (equivalent to the location of that same blue value in the current image).
				}
			}
			
			//data[0].length returns the height of the image.
			//data.length returns the width of the image.
			holderX = new int[data[0].length]; //x  - holds the x-keys, right now we're giving it a size to accommodate the width of the image (holds all the random values of each row)     //data[0].length returns the number of rows in the data array, thus data[0].length is the number of keys that need to be generated to move each row of data a unique number of positions along the x axis.
			holderY = new int[data.length]; //y     - holds the y-keys, right now we're giving it a size to accommodate the height of the image (holds all the random values of each column)  //data.length returns the number of columns in the data array, thus data.length is the number of keys that need to be generated to move each column of data a unique number of positions along the y axis.
			
			//holderX holds the number of keys needed to move each row of the image a unique number of pixels along the x-axis.  Therefore, holderX will have the same size as the height of the image.
			//holderY holds the number of keys needed to move each column of the image a unique number of pixels along the y-axis.  Therefore, holderY will have the same size as the width of the image.
			
			System.out.println(holderX.length + " " + holderY.length); //Prints out the number of keys needed to move all the rows of the image (holderX), and the number of keys needed to move all the columns of the image (holderY).  E.g., it prints the height, and then the width of the image.
			
			//Key generation - from x=0 and y=0, to x = 0, y = image height - 1.
			//I.e., iterates over the height of the image so that for each row a random number is generated to move that row a unique number of spots left or right along the x-axis.
			for(int i = 0; i < holderX.length; i++) { //iterates over the length of the holderX variable, and thus goes will go from 0 to the height of the image -1.
				x = rnd.nextInt(data.length); //integer variable x is set equal to a random number between 0 (inclusive) and data.length (exclusive) and thus is capable of returning a value between 0 and the width of the image -1.
				holderX[i] = x; //holderX variable at index i is set equal to the newly generated random number.
				System.out.print(x + ", "); //x variable is printed to the console.
			}
			System.out.print('\n'); //prints a newline character to the console.
			
			//Key generation - from x=0 and y=0, to x=image width - 1 and y=0.
			//I.e., iterates over the width of the image so that for each column a random number is generated to move that column a unique number of spots up or down along the y-axis.
			for(int i = 0; i < holderY.length; i++) { //iterates over the length of the holderY variable, and thus goes will go from 0 to the width of the image -1.
				y = rnd.nextInt(data[0].length);   //integer variable y is set equal to a random number between 0 (inclusive) and data[0].length (exclusive) and thus is capable of returning a value between 0 and the height of the image -1.
				holderY[i] = y; //holderY variable at index i is set equal to the newly generated random number.
				System.out.print(y + ", "); //y variable is printed to the console.
			}
			System.out.print('\n'); //newline character is printed.
			
			for(int i = 0; i < data.length; i++) {  //iterates over the same number of pixels as the width of the image
				ShiftDown(data, i, data[0].length, holderY[i]); //passes in the data array, the current column to be moved down (i), the height of the column (data[0].length), and how many positions down it needs to be shifted.
				ShiftDown(data1, i, data[0].length, holderY[i]); //passes in the data1 array, the current column to be moved down (i), the height of the column (data[0].length), and how many positions down it needs to be shifted.
				ShiftDown(data2, i, data[0].length, holderY[i]); //passes in the data2 array, the current column to be moved down (i), the height of the column (data[0].length), and how many positions down it needs to be shifted.
			}
			for (int y = 0; y < holderX.length; y++) { //iterates over the same number of pixels as the height of the image
			    ShiftLeft(data,  y, data.length, holderX[y]); //passes in the data array, the current row to be moved left (y), the width of the row (data.length), and how many positions left it needs to be shifted.
			    ShiftLeft(data1, y, data1.length, holderX[y]); //passes in the data1 array, the current row to be moved left (y), the width of the row (data.length), and how many positions left it needs to be shifted.
			    ShiftLeft(data2, y, data2.length, holderX[y]); //passes in the data2 array, the current row to be moved left (y), the width of the row (data.length), and how many positions left it needs to be shifted.

			}
			saveImage(k, contentType);  //call the saveImage method, pass in integer variable k and String variable contentType.
			saveText(k); //call the saveText method and pass in integer variable k.
		}
	}
	
	
	
	/**LoadImage
	 * 
	 * @param path
	 * @return
	 */
	
	public static BufferedImage loadImage(String path) {
		try {
			path = path.replace("\\", "/");//replace all instances in the String path of "\\" with "/".  This is necessary for the next step.
			sourceFile = new File(path); //File sourceFile is set equal to a new file and given the string variable path as a path.
			String filetype = path.substring(path.lastIndexOf(".")+1); //sets the String variable filetype equal to the text in the string path after the '.' character
			if (path.endsWith(".png")) { //if the ending of the path string variable is .png
				contentType = "PNG";  //then this file is a .png file and String variable contentType is set equal to "PNG"
				return ImageIO.read(sourceFile); //this method returns the data in the sourceFile.
		    } else if (path.endsWith(".jpg") || path.endsWith(".jpeg")) { //else if the String variable path ends with .jpg or .jpeg
		    	BufferedImage img = ImageIO.read(sourceFile); //temporary BufferedImage img is set equal to the data in the sourceFile.
		    	File outputFile = new File(sourceFile.toString().replace(filetype, ".png")); //the output file path is given the ".png" extension
	            ImageIO.write(img, "png", outputFile); //the BufferedImage img variable is written to the outputFile and given the .png type.
	            delete = true; //a boolean that will be used to delete the original non-png sourceFile.
	            return ImageIO.read(outputFile); //reads the new png outputFile
		    }
		}catch(IOException e) {  //Catches Input/Output Exceptions
			e.printStackTrace(); //prints where the screw up happened if one did.
			System.exit(1); //Terminated the program.
		}
		return null;  //returns null
	}
	
	public static void deleteExtraneousFile() { //deletes the old non-png file
		if(delete) { //delete is only true when there was an original non-png file
			sourceFile.delete(); //deletes the original non-png sourceFile
			delete = false; //delete is set back equal to false so as to not delete every image that comes after a non-png source file.
		}
	}
	
	/**Save the encrypted image
	 * 
	 * @param path
	 * @param state
	 * @return
	 */
	
	public static BufferedImage saveImage(int k, String fileType) {  //String fileType parameter is new.
		for (int y = 0; y < height[k]; y++) {  //iterates over the height of the image (the rows)
            for (int x = 0; x < width[k]; x++) { //iterates over the width of the image (the columns)
                // Ensure values are in 0xRRGGBB format
            	int value = data[x][y]; //integer variable value is set equal to the red value of the data array at index x and y
            	int value1 = data1[x][y]; //integer variable value1 is set equal to the green value of the data array at index x and y
            	int value2 = data2[x][y]; //integer variable value2 is set equal to the blue value of the data array at index x and y
				int argb = (255 << 24) | (value << 16) | (value1 << 8) | value2;  //ensures values are in 0xRRGGBB format.
				bf1[k].setRGB(x, y, argb); //sets the BufferedImage array object bf1's value at index[k] (the current image) and at the current x and y coordinates equal to the new RGB value for the pixel. 
            }
        }
		try {
            File outputFile = new File("encrypted/" + fileNameWithOutExt + ".png"); //Creates a file at the path specified.  (Even .jpg or .jpeg files).  Also writes them to the encrypted folder.

            ImageIO.write(bf1[k], "PNG", outputFile); //Will write all data in the current image bf1 (determined by k) as a .png type to the outputFile.
            
            System.out.println(contentType + " file created successfully at: " + outputFile.getAbsolutePath());//prints the file content type, and that it was successfully printed at the absolute path of its location.
        } catch (IOException e) {//Catching Input/ Output Exceptions
            System.err.println("Error saving the " + contentType + " file: " + e.getMessage());  //Prints that there's been an error
        }
		return null; //returns null
	}	
	
	public static void saveText(int k) {  //saves the decryption key of the current image.
		String filename = "dec_keys/" + fileNameWithOutExt + ".txt";  //String variable filename is set equal to the String of the desired path location.
		BufferedWriter outputWriter = null; //BufferedWriter object outputWriter is declared.
		try {
			outputWriter = new BufferedWriter(new FileWriter(filename)); //tries to write a file to this location
		} catch (IOException e) {//catches any input/ output exceptions.
			// TODO Auto-generated catch block
			e.printStackTrace();//prints where the screw up was.
		}
		for (int i = 0; i < holderY.length; i++) { //iterates over the width of the image
		    // Maybe:
		    try {
				outputWriter.write(holderY[i]+", "); //writes the decryption key values to the file with a comma delimiter.  Note that holderY holds the positions the column was moved either up or down.
			} catch (IOException e) {  //catches any input/ output exceptions.
				// TODO Auto-generated catch block
				e.printStackTrace();//prints where the screw up was.
			}
		  }
		try {
			outputWriter.write('\n');  //prints a newline character.
		} catch (IOException e) { //catches any input/output exceptions.
			// TODO Auto-generated catch block
			e.printStackTrace();//prints where the screw up was.
		}
		
		for (int i = 0; i < holderX.length; i++) { //iterates over the height of the image
		    // Maybe:
		    try {
				outputWriter.write(holderX[i]+", ");//writes the decryption key values to the file with a comma delimiter.  Note that holderX holds the positions the row was moved either left or right
			} catch (IOException e) { //catches any input/output exceptions.
				// TODO Auto-generated catch block
				e.printStackTrace();//prints where the screw up was.
			}
		  }
		  try {
			outputWriter.flush();  //removes all contents in the outputWriter.
		} catch (IOException e) { //catches any input/output exceptions.
			// TODO Auto-generated catch block
			e.printStackTrace();//prints where the screw up was.
		}  
		  try {
			outputWriter.close(); //closes the outputWriter.
		} catch (IOException e) { //catches any input/output exceptions.
			// TODO Auto-generated catch block
			e.printStackTrace();//prints where the screw up was.
		} 
	}
	
	public static void getFileNames() {
		try {
            // Get the path of the running JAR (or class if run in IDE)
            File jarFile = new File(
                PathResolver.class
                    .getProtectionDomain()
                    .getCodeSource()
                    .getLocation()
                    .toURI()
            );
            
            // Get the directory containing the JAR
            File baseDir = jarFile.getParentFile();

            // Example folders in same directory as JAR
            File inputFolder = new File(baseDir, "resources");
            
            //gets the complete path to the resources folder
            Path path = Paths.get(inputFolder.toString());
            
            //tries to return the Paths for each file in the folder
            try(Stream <Path> subPaths = Files.walk(path)){
    			//subPaths.filter(Files::isRegularFile).forEach(System.out::println);  For testing purposes
            	
            	//Only files matching the criteria are put into the subPathsList String ArrayList.
    			subPathsList = subPaths.filter(Files::isRegularFile).map(Objects::toString).collect(Collectors.toList());
    			
    			//All image file paths are printed to the console.
    			System.out.println(subPathsList);
    			
    		}catch(IOException e) { //Catches Input/ Output Exceptions
    			e.printStackTrace();  //Prints where the error occurred
    			System.exit(1); //Terminates
    		}   
        } catch (URISyntaxException e) { //Catches the error if the text cannot be parsed.
            e.printStackTrace();
        }
	}
	
}