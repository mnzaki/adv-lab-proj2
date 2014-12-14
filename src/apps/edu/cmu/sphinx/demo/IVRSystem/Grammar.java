package edu.cmu.sphinx.demo.IVRSystem;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Matchable {
	public Pattern pattern;
	public String tag;
	public Matchable(Pattern pattern, String tag) {
		this.pattern = pattern;
		this.tag = tag;
	}
	
	@Override
	public String toString() {
		return this.pattern.toString() + " => " + this.tag;
	}
}

// A Grammar object provides a .match() method which:
// accepts a string
// determines if the string matches the grammar
// 		if it matches, returns a list of tag strings
//      if not returns null
//
// it works by parsing an XML grammar into a set of
// regular expressions that can be matched against to determine
// a set of tag strings to be activated
public class Grammar {
	// a map of rule IDs to sets of matchables
	private Hashtable<String, ArrayList<Matchable>> rules;
	private String rootRule;

	public Grammar(Node node) throws Exception {
		if (!node.type.equals("grammar")) {
			throw new Exception("Need a <grammar> node!");
		}
		rules = new Hashtable<String, ArrayList<Matchable>>();

		rootRule = node.getAttr("root");
		if (rootRule == null || rootRule.equals("")) {
			throw new Exception("Grammar has no root rule!");
		}
		
		rootRule = rootRule.trim();
		
		for (Node child: node.children) {
			String id = child.getAttr("id").trim();
			if (id == null || id.equals("")) {
				throw new Exception("Rule has no ID!");
			}
			id = id.trim();
			
			ArrayList<Matchable> matchables = ruleNodeToMatchables(child);
			rules.put(id, matchables);
		}
	}

	private String generateRegexForNode(Node node) {
		// FIXME use (?:) instead to avoid capture
		String ret = "";
		if (node.type.equals("item")) {
			ret += "\\s*" + node.text.trim() + "\\s*";
			for (Node child: node.children) {
				String childRegex = generateRegexForNode(child);
				if (!childRegex.equals("")) {
					ret += "\\s*(?:" + childRegex + ")";
				}
			}
		} else if (node.type.equals("one-of")) {
			for (int i = 0; i < node.children.size(); i++) {
				Node child = node.children.get(i);
				String childRegex = generateRegexForNode(child);
				if (!childRegex.equals("")) {
					if (i > 0) {
						ret += "|";
					}
					ret += childRegex;
				}
			}
		}
		if (!ret.equals("")) {
			ret = "(?:" + ret + ")";
		}
		return ret;
	}
	
	private String generateRegexForTagPath(Stack<Node> stk) {
		String ret = "";
		Node prev = null;
		for (Node cur: stk) {
			if (cur.type.equals("rule")) {
				// the first node will be a rule
				// do nothing except saving it in prev
			} else if (prev.type.equals("item")){
				// for <item>s we generate a regex including
				// children upto but not including the current item
				for (Node child: prev.children) {
					if (child == cur) break;
					ret += generateRegexForNode(child);
				}				
			} else if (prev.type.equals("one-of")) {
				// for <one-of>s we generate a regex including
				// only the current child if it's an item
				if (cur.type.equals("item")) {
					ret += generateRegexForNode(cur);
				} else if (cur.type.equals("tag")) {
					// but if it's a <tag> inside the <one-of>
					// then we need to generate for the entire <one-of>
					ret += generateRegexForNode(prev);
				}
			}
			prev = cur;
		}
		
		return ret;
	}
	
	private void traverse(Stack<Node> stk, ArrayList<Matchable> matchables) {
		Node node = stk.peek();
		if (node.type.equals("tag")) {
			String regex = generateRegexForTagPath(stk);
			Pattern p = Pattern.compile(regex);
			Matchable m = new Matchable(p, node.text);
			matchables.add(m);
		} else {
			for (Node child: node.children) {
				stk.push(child);
				traverse(stk, matchables);
				stk.pop();
			}
		}
	}
	
	private ArrayList<Matchable> ruleNodeToMatchables(Node rule) throws Exception {
		if (!rule.type.equals("rule")) {
			throw new Exception("Need a <rule> node!");
		}
		
		ArrayList<Matchable> matchables = new ArrayList<Matchable>();
		Stack<Node> path = new Stack<Node>();
		path.push(rule);
		traverse(path, matchables);
		
		return matchables;
	}
	
	private ArrayList<Matchable> getRule(String ruleId) {
		return rules.get(ruleId);
	}
	
	@Override
	public String toString() {
		ArrayList<Matchable> matchables = getRule(rootRule);
		String ret = "";
		for (Matchable m: matchables) {
			ret += m + "\n";
		}
		return ret;
	}
	
	public ArrayList<String> match(String inp) {
		ArrayList<String> tags = new ArrayList<String>();
		for (Matchable m: getRule(rootRule)) {
			Matcher matcher = m.pattern.matcher(inp);
			if (matcher.find()) {
				tags.add(m.tag);
			}
		}

		if (tags.size() > 0) return tags;
		else return null;
	}
}
