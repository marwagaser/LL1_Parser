import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.Stack;

public class T14_37_10683_Marwa_Gaser {

	// T14_37_10683_Marwa_Gaser

	static class CFG {
		String grammar;
		static LinkedHashMap<String, String> Firsttable;
		static LinkedHashMap<String, String> Followtable;
		static LinkedHashMap<String, String> FirsttableIndex;
		static LinkedHashMap<String, String> parseTable;

		/**
		 * Creates an instance of the CFG class. This should parse a string
		 * representation of the grammar and set your internal CFG attributes
		 * 
		 * @param grammar
		 *            A string representation of a CFG
		 */
		public CFG(String grammar) {
			this.grammar = grammar;
			Firsttable = new LinkedHashMap<String, String>();
			Followtable = new LinkedHashMap<String, String>();
			FirsttableIndex = new LinkedHashMap<String, String>();
			parseTable = new LinkedHashMap<String, String>();

		}

		public static String[] splitString(String s, String operator) {
			String[] sections = s.split(operator);
			return sections;
		}

		public void getFirst(LinkedHashMap<String, String> hmap) {
			boolean changed = false;
			Firsttable.clear();
			FirsttableIndex.clear();
			Set<String> keys = hmap.keySet();
			for (String key : keys) {
				String value = hmap.get(key);
				String[] sentential_form = splitString(value, ",");
				int length = sentential_form.length;
				for (int i = 0; i < length; i++) {
					Character c = sentential_form[i].charAt(0);
					int ascii = c;
					// 65-90 not upper case (Variables)
					if (!(ascii >= 65 && ascii <= 90)) {
						changed = true;
						if (Firsttable.containsKey(key)) {
							Firsttable.put(key, Firsttable.get(key) + "," + (c.toString()));
							FirsttableIndex.put(key, FirsttableIndex.get(key) + "," + i);

						} else {
							Firsttable.put(key, (c.toString()));
							FirsttableIndex.put(key, "" + i);
						}

						String val = Firsttable.get(key);
						Firsttable.put(key, val);
					} else {
						Firsttable.put(key, "");
						FirsttableIndex.put(key, "");
					}
				}

			}

			while (changed) {
				changed = false;
				for (String key : keys) { // for every rule in the CFG
					String value = hmap.get(key);
					String[] sentential_form = splitString(value, ","); // get its right hand side in an array
					int length = sentential_form.length;
					for (int i = 0; i < length; i++) { // for each sentential form in the RHS
						String[] current = splitString(sentential_form[i], ""); // populate an array with the letters of
																				// that sentential form
						int epsilonCount = 0; // a counter to count the number of variables that go to epsilon in that
												// rule
						for (int j = 0; j < current.length; j++) { // for each letter in the sentential form
							Character x = current[j].charAt(0);
							int ascii = x; // get its ASCII
							if (ascii >= 65 && ascii <= 90 && Firsttable.get(x.toString()).contains("e")) { // if upper
																											// case
																											// and its
																											// first
																											// has
																											// epsilon
								epsilonCount++; // increment the epsilon counter
							} else { // otherwise
								epsilonCount = 0; // set the counter to zero
								break;
							}

						}
						if (epsilonCount == current.length) { // if the count of epsilons = the length of the sentential
																// form, this means that the LHS variable should have
																// epsilon in its First
							// do the if logic
							if (!Firsttable.get(key).contains("e")) { // if the LHS variable doesn't have epsilon in its
																		// First

								String str = Firsttable.get(key) + ",e"; // add epsilon to its First
								Firsttable.put(key, str);
								FirsttableIndex.put(key, FirsttableIndex.get(key) + "," + i);
								changed = true; // set the boolean to true as the Firsttable hash table was modified

							}
						}
						for (int z = 0; z < length; z++) { // For each sentential form
							for (int j = 0; j < current.length; j++) { // loop on every varaible/letter on the RHS of
																		// every
																		// sentential form
								Character x = current[j].charAt(0);
								int ascii = x;
								int epsilonPreceding = 0; // a counter that checks the number of epsilons preceding that
															// variable/letter
								if (j > 0) { // if the letter is at a position greater than the first
									for (int k = 0; k < j; k++) { // loop on every variable/terminal before it
										Character t = current[k].charAt(0);
										int asciiT = t;
										if (asciiT >= 65 && asciiT <= 90
												&& Firsttable.get(t.toString()).contains("e")) { // if
																									// variable
																									// has
																									// an
																									// epsilon
											epsilonPreceding++; // increment epsilonPreceding count
										} else { // otherwise
											epsilonPreceding = 0; // set it to zero
											break;
										}
									}
								}

								if (j == 0 || epsilonPreceding == j) { // if the variable or terminal is at position
																		// zero,
																		// or the all preceding letters have epsilon in
																		// their first

									if (Firsttable.containsKey(current[j])) {
										String dd = Firsttable.get(current[j]);
										if (dd.contains(",e")) {
											dd = dd.replace(",e", "");
										} else if (dd.contains("e")) {
											dd = dd.replace("e", "");
										}
										String[] check = splitString(dd, ",");
										for (int u = 0; u < check.length; u++) {
											if (!(Firsttable.get(key).contains(check[u]))) {

												String str = Firsttable.get(key) + "," + check[u];
												Firsttable.put(key, str);
												FirsttableIndex.put(key, FirsttableIndex.get(key) + "," + z);
												changed = true;

											}
										}
									} else if (!(ascii >= 65 && ascii <= 90)) {
										if (!Firsttable.get(key).contains(x.toString())) {

											String str = Firsttable.get(key) + "," + x;
											Firsttable.put(key, str);
											FirsttableIndex.put(key, FirsttableIndex.get(key) + "," + z);
											changed = true;

										}
									}

								}
							}
						}
					}
				}
			}
		}

		public void getFollow(LinkedHashMap<String, String> hmap) {
			boolean changed = false;
			Followtable.clear();
			Set<String> keys = hmap.keySet();
			boolean setFirstVar = false;
			for (String key : keys) {
				if (setFirstVar == false) {
					Followtable.put(key, "$");
					setFirstVar = true;
					changed = true;
				} else {
					Followtable.put(key, "");
				}
			}
			while (changed) {

				changed = false;
				for (String key : keys) {
					String[] sentential_forms = splitString(hmap.get(key), ",");
					for (int i = 0; i < sentential_forms.length; i++) {
						String[] current = splitString(sentential_forms[i], ""); // word letter by letter
						for (int j = 0; j < current.length; j++) { // for each letter
							Character x = current[j].charAt(0);
							int ascii = x;
							if (ascii >= 65 && ascii <= 90) { // if its a variable
								if (j + 1 < current.length) {
									for (int p = j + 1; p < current.length; p++) {
										String newString;
										Character p1 = current[p].charAt(0);
										int asciiP = p1;
										if (asciiP >= 65 && asciiP <= 90) { // if p is a variable
											if (!(Firsttable.get(current[p]).contains("e"))) { // add and leave
												newString = (Firsttable.get(current[p]));
												String[] newS = splitString(newString, ",");
												for (int h = 0; h < newS.length; h++) {
													if (!(Followtable.get(current[j]).contains(newS[h]))) {
														if (Followtable.get(current[j]).equals("")) {
															String value = newS[h];
															Followtable.put(current[j], value);
															changed = true;
														} else {
															String value = Followtable.get(current[j]) + "," + newS[h];
															HashSet<String> hs = new HashSet<String>(
																	Arrays.asList(value.split(",")));
															value = String.join(",", hs);
															Followtable.put(current[j], value);
															changed = true;
														}
													}

												}
												break;
											} else if ((Firsttable.get(current[p]).contains(",e"))) { // add and stay bc
																										// epsilon
												newString = (Firsttable.get(current[p]).replace(",e", ""));
												String[] newS = splitString(newString, ",");
												for (int h = 0; h < newS.length; h++) {
													if (!(Followtable.get(current[j]).contains(newS[h]))) {
														if (Followtable.get(current[j]).equals("")) {
															String value = newS[h];
															Followtable.put(current[j], value);
															changed = true;
														} else {
															String value = Followtable.get(current[j]) + "," + newS[h];
															HashSet<String> hs = new HashSet<String>(
																	Arrays.asList(value.split(",")));
															value = String.join(",", hs);
															Followtable.put(current[j], value);
															changed = true;
														}
													}

												}
												if (p == current.length - 1) {
													String followLHS = Followtable.get(key);
													String[] LHSarr = splitString(followLHS, ",");
													for (int r = 0; r < LHSarr.length; r++) {
														if (!(Followtable.get(current[j]).contains(LHSarr[r]))) {
															if (Followtable.get(current[j]).equals("")) {
																Followtable.put(current[j], LHSarr[r]);
																changed = true;
															} else {
																String value = Followtable.get(current[j]) + ","
																		+ LHSarr[r];
																HashSet<String> hs = new HashSet<String>(
																		Arrays.asList(value.split(",")));
																value = String.join(",", hs);
																Followtable.put(current[j], value);
																changed = true;
															}
														}
													}
													// add the follow of parent to me too
												}
											} else if ((Firsttable.get(current[p]).contains("e"))) { // add and staybc
																										// epsilon
												newString = (Firsttable.get(current[p]).replace("e", ""));
												String[] newS = splitString(newString, ",");
												for (int h = 0; h < newS.length; h++) {
													if (!(Followtable.get(current[j]).contains(newS[h]))) {
														if (Followtable.get(current[j]).equals("")) {
															String value = newS[h];
															Followtable.put(current[j], value);
															changed = true;
														} else {
															String value = Followtable.get(current[j]) + "," + newS[h];
															HashSet<String> hs = new HashSet<String>(
																	Arrays.asList(value.split(",")));
															value = String.join(",", hs);
															Followtable.put(current[j], value);
															changed = true;
														}
													}

												}
												if (p == current.length - 1) {

													String followLHS = Followtable.get(key);
													String[] LHSarr = splitString(followLHS, ",");
													for (int r = 0; r < LHSarr.length; r++) {
														if (!(Followtable.get(current[j]).contains(LHSarr[r]))) {
															if (Followtable.get(current[j]).equals("")) {
																Followtable.put(current[j], LHSarr[r]);
																changed = true;
															} else {
																String value = Followtable.get(current[j]) + ","
																		+ LHSarr[r];
																HashSet<String> hs = new HashSet<String>(
																		Arrays.asList(value.split(",")));
																value = String.join(",", hs);
																Followtable.put(current[j], value);
																changed = true;
															}
														}
													}
													// add the follow of parent to me too
												}
											}
										} else {

											if (!(Followtable.get(current[j]).contains(current[p]))) {
												if (Followtable.get(current[j]).equals("")) {
													String value = current[p];
													Followtable.put(current[j], value);
													changed = true;
												} else {
													String value = Followtable.get(current[j]) + "," + current[p];
													HashSet<String> hs = new HashSet<String>(
															Arrays.asList(value.split(",")));
													value = String.join(",", hs);
													Followtable.put(current[j], value);
													changed = true;
												}
											}

											break;
										}

									}
								} else if (j + 1 == current.length) {
									// if im the last var my follow is the parents follow
									String followLHS = Followtable.get(key);
									String[] LHSarr = splitString(followLHS, ",");
									for (int r = 0; r < LHSarr.length; r++) {
										if (!(Followtable.get(current[j]).contains(LHSarr[r]))) {
											if (Followtable.get(current[j]).equals("")) {
												Followtable.put(current[j], LHSarr[r]);
												changed = true;
											} else {
												String value = Followtable.get(current[j]) + "," + LHSarr[r];
												HashSet<String> hs = new HashSet<String>(
														Arrays.asList(value.split(",")));
												value = String.join(",", hs);
												Followtable.put(current[j], value);
												changed = true;
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}

		/**
		 * Generates the parsing table for this context free grammar. This should set
		 * your internal parsing table attributes
		 * 
		 * @return A string representation of the parsing table
		 */
		public String table() {
			// 1. convert grammar string to LinkedHashMap
			parseTable.clear();
			LinkedHashMap<String, String> hmap = new LinkedHashMap<String, String>();
			String table = "";
			String[] CFGrules = splitString(this.grammar, ";");
			for (int i = 0; i < CFGrules.length; i++) {
				String key = CFGrules[i].substring(0, 1);
				String value = CFGrules[i].substring(2);
				hmap.put(key, value);
			}

			getFirst(hmap); // pass it to the first algorithm
			getFollow(hmap); // pass it to the follow algorithm
			for (int i = 0; i < CFGrules.length; i++) {
				String key = CFGrules[i].substring(0, 1); // get the key
				String value = CFGrules[i].substring(2); // get its values
				String[] values = splitString(value, ","); // put the values in an array

				// get the first values and its indices
				String firsts = Firsttable.get(key); // get the firsts of that key
				String sententialIndices = FirsttableIndex.get(key); // get the indices of the sentential forms that
																		// made the firsts
				String follows = Followtable.get(key); // get the follows of the key
				String[] followsArr = splitString(follows, ",");
				if (((firsts.charAt(0)) + "").equals(",")) {

					sententialIndices = sententialIndices.substring(1);
					firsts = firsts.substring(1);

				}
				String[] firstsArr = splitString(firsts, ",");
				String[] senArr = splitString(sententialIndices, ",");
				for (int k = 0; k < firstsArr.length; k++) { // for all the firsts of S
					if (!firstsArr[k].equals("e")) {
						if (!(parseTable.containsKey(key))) {

							parseTable.put(key,
									key + "," + firstsArr[k] + "," + values[Integer.parseInt(senArr[k] + "")]);

						} else {

							String val = (parseTable.get(key)) + ";" + key + "," + firstsArr[k] + ","
									+ values[Integer.parseInt(senArr[k] + "")];
							parseTable.put(key, val);
						}
					} else {
						for (int f = 0; f < followsArr.length; f++) {

							if (!(parseTable.containsKey(key))) {
								// get
								parseTable.put(key, key + "," + followsArr[f] + ",e");

							} else {

								String val = (parseTable.get(key)) + ";" + key + "," + followsArr[f] + ",e";
								parseTable.put(key, val);
							}
						}
					}

				}

			}
			Set<String> keys = parseTable.keySet();

			for (String key : keys) {
				table = table + parseTable.get(key) + ";";
			}

			return table.substring(0, table.length() - 1);
		}

		/**
		 * Parses the input string using the parsing table
		 * 
		 * @param s
		 *            The string to parse using the parsing table
		 * @return A string representation of a left most derivation
		 */
		public String parse(String s) {
			s=s+"$";
			Stack<String> stack = new Stack<String>();
			ArrayList<String> derivation = new ArrayList<String>();
			derivation.add(this.grammar.charAt(0) + "");
			stack.push(this.grammar.charAt(0) + ""); // push the start symbol on top of the stack
			while (!stack.isEmpty()) {

				  if ((s.charAt(0) + "").equals(stack.peek())) {
					if (s.length() > 1) {
						s = s.substring(1);
						stack.pop();
					} else if (s.length() == 1) {
						stack.pop();
					}

				} else if (!(((int) stack.peek().charAt(0) >= 65) && ((int) stack.peek().charAt(0) <= 90))) { // if top
																												// of
																												// the
																												// stack
																												// is a
																												// terminal
					derivation.add("ERROR");
					break;
				} else if (parseTable.containsKey(stack.peek())) {
					// search for X and current character combination and get the third one.
					// if it doesn't exist produce an error
					String[] KeyParseTable = splitString(parseTable.get(stack.peek()), ";"); // parse table of the
																								// current key
					boolean found = false;// check if i looped in all the row cells and i didn't find it then error
					for (int i = 0; i < KeyParseTable.length; i++) {
						String[] threeTerms = splitString(KeyParseTable[i], ",");
						if ((threeTerms[1]).equals(s.charAt(0) + "")) {
							String toBePushed = threeTerms[2];
							int index = derivation.size() - 1;
							if (toBePushed.equals("e")) {
								String newDerivation = derivation.get(index).replaceFirst(stack.peek(), "");
								derivation.add(newDerivation);
								stack.pop();
								found = true;
								break;
							} else {

								String newDerivation = derivation.get(index).replaceFirst(stack.peek(), toBePushed);
								derivation.add(newDerivation);
								stack.pop();
								for (int k = toBePushed.length() - 1; k >= 0; k--) {
									stack.push(toBePushed.charAt(k) + "");
								}
								found = true;
								break;
							}

						}

					}
					if (found == false) {
						derivation.add("ERROR");
						break;
					}
				}
				if (derivation.get(derivation.size() - 1).equals("ERROR")) {
					break;
				}

			}
			return derivation.toString().substring(1,derivation.toString().length()-1);
		}

		public static void printarray(ArrayList<String> x) {
			for (int i = 0; i < x.size(); i++) {
				System.out.print(x.get(i) + " ");
			}
		}
	}

	public static void main(String[] args) {

		/*
		 * Please make sure that this EXACT code works. This means that the method and
		 * class names are case sensitive
		 */

		String grammar = "S,iST,e;T,cS,a";
		// String grammar = "S,AB;A,iA,n;B,CA;C,xC,y";
		String input1 = "iiac";
		String input2 = "iia";
		CFG g = new CFG(grammar);
		System.out.println(g.table());
		System.out.println(g.parse(input1));
		System.out.println(g.parse(input2));

	}

}
