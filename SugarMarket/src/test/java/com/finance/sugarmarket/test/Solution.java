package com.finance.sugarmarket.test;

import java.util.HashMap;
import java.util.Map;

class Solution {

	public class TreeNode {
		int val;
		TreeNode left;
		TreeNode right;

		TreeNode() {
		}

		TreeNode(int val) {
			this.val = val;
		}

		TreeNode(int val, TreeNode left, TreeNode right) {
			this.val = val;
			this.left = left;
			this.right = right;
		}
	}

	public boolean buddyStringsSub(String s, String goal, boolean doneOnce) {
		if (s.length() != goal.length())
			return false;
		if(s.charAt(0) == goal.charAt(0) && s.length() == 1){
			return doneOnce;
        }
		if (s.charAt(0) != goal.charAt(0)) {
			if(doneOnce)
				return false;
			char[] tempArr = s.toCharArray();
			boolean found = false;
			for (int i = 1; i < tempArr.length; i++) {
				if (tempArr[i] == goal.charAt(0)) {
					found = true;
					char temp = tempArr[i];
					tempArr[i] = s.charAt(0);
					tempArr[0] = temp;
				}
			}
			if(!found)
				return false;
			s = new String(tempArr);
			doneOnce = true;
		}
		
		return buddyStringsSub(s.substring(1), goal.substring(1), doneOnce);
	}
	
	public boolean haveSameChar(String s) {
		Map<Character, Boolean> map = new HashMap<Character, Boolean>();
		for(char c : s.toCharArray()) {
			if(map.get(c) != null && map.get(c))
				return true;
			map.put(c, true);
		}
		return false;
	}
	
	public boolean buddyStrings(String s, String goal) {
		return buddyStringsSub(s, goal, false) && haveSameChar(s);
    }
}