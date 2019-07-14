/*--- (C) 1999-2018 Techniker Krankenkasse ---*/

package org.stringtemplate.bazel;

import java.util.List;

import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.Tree;
import org.assertj.core.util.Arrays;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.compiler.CompiledST;

public class Test {

	public static void main(String[] args) {
		ST st = new ST("<user.name> []");

		System.out.println(st.impl.instrs());
		System.out.println(Arrays.asList(st.impl.strings));
		
	}

	private static void dump(Tree tree) {
		for (int i = 0, size = tree.getChildCount(); i < size; i++) {
			Tree c = tree.getChild(i);
			System.out.println(c.getText() + " " + c.getType());
			dump(c);
		}
	}

}

/*--- Formatiert nach TK Code Konventionen vom 05.03.2002 ---*/
