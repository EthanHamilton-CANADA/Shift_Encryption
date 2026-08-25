package no_edits; //package

import java.awt.Color; //Libraries

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

public class Decrypt { //Class - This program will decrypt data.
	
	/**
	 * THIS PROGRAM WILL DECRYPT DATA
	 */
	static Random rnd = new Random();	//rnd object of type Random is declared and initialized.
	private static int x = 0;	//unused
	private static int y = 0;	//unused
	private static int[] holderX;	//holderX array, will go onto hold the values needed to move each row x positions to the left or right.  Thus, it will also have a length equivalent to the height of an image.
	private static int[] holderY;	//holderY array, will go onto hold the values needed to move each column y positions up or down.  Thus, it will also have a length equivalent to the width of an image.
	private static BufferedImage[] bf1;	//BufferedImage array bf1 will store all the images.
	private static int[] width;	//integer width array will go onto store the width of each image
	private static int[] height;	//integer height array will go onto store the height of each image
	private static int[][] data;	//two-dimensional integer array data will store all the red values in each image.
	private static int[][] data1;	//two-dimensional integer array data1 will store all the green values in each image.
	private static int[][] data2;	//two-dimensional integer array data2 will store all the blue values in each image.
	private static List<String> subPathsList;	//List of type string will eventually go onto hold all the encrypted images paths. 
	private static List<String> keyPathsList; 	//List of type string will eventually go onto hold all the decryption keys paths.
	private static String contentType;	//string contentType will store the filetype of each file.
	private static int a = 0;	//unused
	private static int increment = 0;	//unused
	private static String keyNameWithOutExt;	//will go onto store the name of the key file without its type (.txt) extension.
	private static String encNameWithOutExt;	//will go onto store the name of the encrypted file without its (.png) extension.
	private static String fileNameWithOutExt;	//will go onto store the name of the final decrypted file without its (.png) extension.
	
	public static void main(String[] args) {
		getEncryptedFileNames();
		getKeyFileNames();
		InitializeImages(); 
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
		if (n < 1) return; //correct
		
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
			tmp[i] = array[n-shift+i][row];
		}
	    for (int i = n - 1; i > 0+(shift-1); i--) { 
	        array[i][row] = array[i - shift][row]; 
	    }
	    for(int i = shift-1; i >= 0; i--) {
			array[i][row] = tmp[i]; 
		}
	}
	
	
	public static void InitializeImages() {  //InitializeImages method
		bf1 = new BufferedImage[subPathsList.size()];	//sets one-dimensional BufferedImage array bf1 equal to a size to accommodate all the images to be decrypted.
		width = new int[subPathsList.size()];			//sets one-dimensional integer array width equal to a size to accommodate all the images to be decrypted.
		height = new int[subPathsList.size()];			////sets one-dimensional integer array height equal to a size to accommodate all the images to be decrypted.
		for(int k = 0; k < subPathsList.size(); k++) {  //iterates over each image.
			bf1[k] = loadImage(subPathsList.get(k).trim());	//bf1 BufferedImage at index k is set equal to the contents within the image at the path subPathsList.get(k).trim()
  			width[k] = bf1[k].getWidth();				//width of the kth image is put into the one-dimensional array variable width at index k.
			height[k] = bf1[k].getHeight();				//height of the kth image is put into the one-dimensional array variable height at index k.
			data = new int[width[k]][height[k]];		//two-dimensional data array (used to store the red values of each pixel) is given the same size as the width and height of the image.
			data1 = new int[width[k]][height[k]]; 		//two-dimensional data1 array (used to store the green values of each pixel) is given the same size as the width and height of the image.
			data2 = new int[width[k]][height[k]];		//two-dimensional data2 array (used to store the blue values of each pixel) is given the same size as the width and height of the image.
			for(int j = 0; j < height[k]; j++) {		//iterates over the height of the image.
				for(int i = 0; i < width[k]; i++) {		//iterates over the width of the image.
					int rgb = bf1[k].getRGB(i, j);		//integer variable rgb is set equal to the rgb values of the kth image in the bf1 array and at the proper x and y coordinates as determined by variables i and j.
					Color color = new Color(rgb);		//Color variable color is set equal to a new color equivalent to the rgb variable color.
					int red = color.getRed(); 			//integer variable red has the red component of the color variable put into it.
					data[i][j] = red;					//two-dimensional data array at the proper x and y coordinates (as determined by i and j) is set equal to the specific red value.
					
					int green = color.getGreen(); 		//integer variable green is set equal to the green component in the colour variables.
					data1[i][j] = green;				//two-dimensional data1 array at the proper x and y coordinates (as determined by i and j) is set equal to the specific green value.
					
					int blue = color.getBlue(); 		//integer variable blue is set equal to the blue component in the colour variables.
					data2[i][j] = blue;					//two-dimensional data2 array at the proper x and y coordinates (as determined by i and j) is set equal to the specific blue value.
				}
			}
			holderX = new int[data[0].length]; //x  - holds the x-keys, right now we're giving it a size to accommodate the height of the image (holds all the random values of each row).  This variable will store each key that is needed to move a certain row nth positions along the x-axis.
			holderY = new int[data.length]; //y     - holds the y-keys, right now we're giving it a size to accommodate the width of the image (holds all the random values of each column).  This variable will store each key that is needed to move a certain column nth positions along the y-axis.
			System.out.println(holderX.length + " " + holderY.length); //Prints out the width and height of the image.
			
			for(int l = 0; l < keyPathsList.size(); l++) {  //Iterates over the number of decryption keys.
				Path p = Paths.get(keyPathsList.get(l).trim());	//Path object p is set equal to the path of the lth key file minus all the parent folders.
				keyNameWithOutExt = p.getFileName().toString(); //String variable keyNameWithOutExt is set equal to the string of the p variable/
				keyNameWithOutExt = keyNameWithOutExt.replaceFirst("[.][^.]+$", "");	//removes the .txt extension on the string holding the key names.
				
				Path p1 = Paths.get(subPathsList.get(k).trim());//Path object p1 is set equal to path of the kth image file minus all the parent folders.
				encNameWithOutExt = p1.getFileName().toString();//the name of the encrypted path of the encrypted files are put into the encNameWithOutExt string variable.  
				encNameWithOutExt = encNameWithOutExt.replaceFirst("[.][^.]+$", "");	//encNameWithOutExt has its extension/ filetype removed (in this case, it would have its .png extension removed)
				
				if(keyNameWithOutExt.contains(encNameWithOutExt)) {	//if the keyNameWithOurExt contains the same text within the encNameWithOutExt (if the key matches the encrypted file)
					System.out.println(keyPathsList.get(l).trim()); //prints out the name of the file without the extension.
					fileNameWithOutExt = encNameWithOutExt; //string variable fileNameWithOutExt is set equal to the encNameWithOutExt.
					ReadKeys(keyPathsList.get(l).trim());	//Readkeys method is called and passed in the path of the current decryption key file.
					break;
				}else if(l == keyPathsList.size()-1) {  //if that was the last key...
					break;	//break from this loop.
				}else{	//else, if it wasn't the last key...
					continue;	//repeat this loop.
				}
			}
			for(int i = 0; i < data[0].length; i++) {  //iterates over the height of the image.
				ShiftRight(data, i, data.length, holderX[i]);	//shifts the current row (ith row) of the two-dimensional data array by the value in holderX at index i.  data.length is just the length of the row.
				ShiftRight(data1, i, data.length, holderX[i]);	//shifts the current row (ith row) of the two-dimensional data1 array by the value in holderX at index i.  data.length is just the length of the row.
				ShiftRight(data2, i, data.length, holderX[i]);  //shifts the current row (ith row) of the two-dimensional data2 array by the value in holderX at index i.  data.length is just the length of the row.
		}
			for(int i = 0; i < data.length; i++) { //iterates over the width of the image.
				ShiftUp(data, i, data[0].length, holderY[i]);	//shifts the current column (ith column) of the two-dimensional data array by the value in holderY at index i.  data[0].length is just the length of the column.
				ShiftUp(data1, i, data[0].length, holderY[i]);  //shifts the current column (ith column) of the two-dimensional data1 array by the value in holderY at index i.  data[0].length is just the length of the column.
				ShiftUp(data2, i, data[0].length, holderY[i]);  //shifts the current column (ith column) of the two-dimensional data2 array by the value in holderY at index i.  data[0].length is just the length of the column.
			}
			saveImage(k);  //calls the saveImage method and passes in integer variable k (the integer index of the current image).
		}
	}
	
	public static void ReadKeys(String path) {  //public static void ReadKeys Method
	    try (BufferedReader dataReader = new BufferedReader(new FileReader(path))) { //BufferedReader object dataReader is set equal to a new BufferedReader that is passed a FileReader object which in turn is passed a path string variable.

	        String line = dataReader.readLine(); //String variable line is set equal to the read information from the dataReader object.
	        if (line != null) {	//if the line exists...
	            String[] lineItems = line.split(",");	//String array lineItems populates each position in its array for each comma-separated value in the line.
	            for (int i = 0; i < lineItems.length && i < holderY.length; i++) {	//iterates over the lineItems...
	                holderY[i] = Integer.parseInt(lineItems[i].trim());	//holderY at index i is set equal to the value at lineItems at index i.
	            }
	        }

	        line = dataReader.readLine();  //String variable line is set equal to the next line of read information from the dataReader object.
	        if (line != null) {	//if the line exists...
	            String[] lineItems = line.split(",");	//String array lineItems populates each position in its array for each comma-separated value in the line.
	            for (int i = 0; i < lineItems.length && i < holderX.length; i++) {	//iterates over the lineItems...
	                holderX[i] = Integer.parseInt(lineItems[i].trim());	//holderX at index i is set equal to the value at lineItems at index i.
	            }
	        }

	    } catch (Exception e) {	//catches possible errors.
	        System.out.println("Actual error:");	//prints text to console.
	        e.printStackTrace();	//prints out where the error was.
	    }
	}
	
	public static BufferedImage loadImage(String path) {  //loadImage method of type BufferedImage that takes in a String called path.
		try {
			File sourceFile = new File(path);	//File object sourceFile is set equal to a new File at the given path string.
			if (path.endsWith(".png") || path.endsWith(".jpeg")) {	//if the path string ends with .png or .jpeg (only .png files will be in the encrypted folder)
				contentType = "PNG";	//contenType string is set equal to "PNG"
		    } else if (path.endsWith(".jpg") || path.endsWith(".jpeg")) {	//if the path ends with .jpg or .jpeg then... (this will never trigger)
		    	contentType = "JPG";	//contentType string is set equal to "JPG"
		    }
			return ImageIO.read(sourceFile);	//returns the read the sourceFile.
		}catch(IOException e) {	//catches any possible input/ output exception errors
			e.printStackTrace();	//prints where the error was.
			System.exit(1);	//terminates program.
		}
		return null;	//returns null if nothing else is returned.
	}
	
	public static BufferedImage saveImage(int k) { //saveImage method of type BufferedImage and takes in integer variable k.
		for (int y = 0; y < height[k]; y++) {	//iterates over the height of the image.
            for (int x = 0; x < width[k]; x++) {	//iterates over the width of the image.
                // Ensure values are in 0xRRGGBB format
            	int value = data[x][y];	//integer variable value is set equal to the data array at position x and y.  (data array stores the red values) 
            	int value1 = data1[x][y];	//integer variable value1 is set equal to the data1 array at position x and y.  (data1 array stores the green values)
            	int value2 = data2[x][y];	//integer variable value2 is set equal to the data2 array at position x and y.  (data2 array stores the blue values)
            	
				int argb = (255 << 24) | (value << 16) | (value1 << 8) | value2;	//puts rgb values in the proper 0xRRGGBB format.
				bf1[k].setRGB(x, y, argb);	//at BufferedImage array bf1's kth index (the current image) the pixel at the appropriate x and y coordinates is set equal to the proper colour.
            		
				
            	}
            }
		try {
			File outputFile = new File("decrypted/" + fileNameWithOutExt + "." + contentType.toString().toLowerCase());	//File object outputFile is set equal to a new File object in the decrypted file + the file name + . + the desired file type (png).
            ImageIO.write(bf1[k], contentType, outputFile);	//the image is written to the outputFile of the contentType.
            System.out.println(contentType + " file created successfully at: " + outputFile.getAbsolutePath());	//prints that the file has been successfully created at the desired path.
        } catch (IOException e) {
            System.err.println("Error saving the " + contentType + " file: " + e.getMessage());	//There's been an error.
        }
		return null;
	}
	
	public static void getKeyFileNames() {  //method to get the names of the key files
		try {	//tries...
			File jarFile = new File(
	                PathResolver.class
	                    .getProtectionDomain()
	                    .getCodeSource()
	                    .getLocation()
	                    .toURI()
	            );		//gets the location of the this jar file.
	            
	            // Get the directory containing the JAR
	            File baseDir = jarFile.getParentFile();

	            // Example folders in same directory as JAR
	            File inputFolder = new File(baseDir, "dec_keys");	//gets the dec_keys folder.
		
		Path path = Paths.get(inputFolder.toString());	//Path object path is given the same address as the dec_keys folder
		try(Stream <Path> subPaths = Files.walk(path)){	//returns a list of files starting from the path file to files further up the parent chain.
			//subPaths.filter(Files::isRegularFile).forEach(System.out::println);  For testing purposes
			keyPathsList = subPaths.filter(Files::isRegularFile).map(Objects::toString).collect(Collectors.toList());//returns a list of all paths to the keys.
			
			System.out.println(keyPathsList);//prints out the list.
		}catch(IOException e) {	//catches any input/ output exceptions...
			e.printStackTrace();	//prints where the errors came from.
			System.exit(1);	//terminates program.
		}
		}catch(URISyntaxException e) {	//catches any syntax exceptions
            e.printStackTrace();	//prints where the error came from.
        }
	}
	public static void getEncryptedFileNames() {
		try {
		File jarFile = new File(
                PathResolver.class
                    .getProtectionDomain()
                    .getCodeSource()
                    .getLocation()
                    .toURI()
            );//gets the location of the this jar file.
            
            // Get the directory containing the JAR
            File baseDir = jarFile.getParentFile();

            // Example folders in same directory as JAR
            File inputFolder = new File(baseDir, "encrypted"); //gets the encrypted folder.
            
            Path path = Paths.get(inputFolder.toString());	//Path object path is given the same address as the encrypted folder
            try(Stream <Path> subPaths = Files.walk(path)){	//returns a list of files starting from the path file to files further up the parent chain.
    			//subPaths.filter(Files::isRegularFile).forEach(System.out::println);  For testing purposes
    			subPathsList = subPaths.filter(Files::isRegularFile).map(Objects::toString).collect(Collectors.toList());//returns a list of all paths to the keys.
    			System.out.println(subPathsList);//prints out the list.
    		}catch(IOException e) {	//catches any input/ output exceptions...
    			e.printStackTrace();//prints where the errors came from.
    			System.exit(1);//terminates program.
    		}   
        } catch (URISyntaxException e) {//catches any syntax exceptions
            e.printStackTrace();//prints where the error came from.
        }
	}
	
	
}