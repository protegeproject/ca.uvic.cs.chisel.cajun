package ca.uvic.cs.chisel.cajun.util;

import java.util.Comparator;

/**
 * Compares a collection of objects using the {@link Object#toString()} method
 * and doing a string comparison.
 * Can ignore case or invert the comparison as well.
 *
 * @author Chris
 * @since  21-Dec-07
 */
public class ToStringComparator implements Comparator<Object> {

		private boolean ignoreCase;
		private boolean invert;

		public ToStringComparator() {
			this(true, false);
		}
		
		public ToStringComparator(boolean ignoreCase) {
			this(ignoreCase, false);
		}
		
		public ToStringComparator(boolean ignoreCase, boolean invert) {
			this.ignoreCase = ignoreCase;
			this.invert = invert;
		}
		
		public int compare(Object o1, Object o2) {
			String s1 = String.valueOf(o1);
			String s2 = String.valueOf(o2);
			if (invert) {
				String temp = s1;
				s1 = s2;
				s2 = temp;
			}
			return (ignoreCase ? s1.compareToIgnoreCase(s2) : s1.compareTo(s2));
		}
		
	}