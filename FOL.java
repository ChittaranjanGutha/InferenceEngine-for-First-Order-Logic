import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

class Predicate {
	boolean negation;
	String predicate;
	ArrayList<String> values = new ArrayList<String>();

	public Predicate(String v, boolean neg, ArrayList<String> input) {
		predicate = v;
		negation = neg;
		values.addAll(input);
	}
}

public class FOL {
	static String algo;
	static int inputs;
	static int kbsize;
	static ArrayList<ArrayList<Predicate>> kb = new ArrayList<ArrayList<Predicate>>();
	static ArrayList<ArrayList<Predicate>> query = new ArrayList<ArrayList<Predicate>>();
	static ArrayList<ArrayList<Predicate>> generated = new ArrayList<ArrayList<Predicate>>();
	static ArrayList<ArrayList<Predicate>> intermediatae = new ArrayList<ArrayList<Predicate>>();

	// Printing the object with respective values
	void printobject(ArrayList<ArrayList<Predicate>> query) {
		for (int i = 0; i < query.size(); i++) {
			for (int va = 0; va < query.get(i).size(); va++) {
				System.out.print(query.get(i).get(va).negation + " " + query.get(i).get(va).predicate + " "
						+ query.get(i).get(va).values + "|");
			}
			System.out.println();
		}
		System.out.println("===========================================");
	}

	void printsinglepredicate(ArrayList<Predicate> query) {
		for (int i = 0; i < query.size(); i++) {
			System.out.print(query.get(i).negation + " " + query.get(i).predicate + " " + query.get(i).values + "|");
		}
		System.out.println();
	}

	// Check if the generated statement is already present in the KB
	boolean check_GeneratedPredicate(ArrayList<Predicate> res) {
		for (int index = 0; index < res.size(); index++) {
			for (int i = 0; i < generated.size(); i++) {
				for (int j = 0; j < generated.get(i).size(); j++) {
					if (generated.get(i).get(j).predicate.equals(res.get(index).predicate)) {
						if (generated.get(i).get(j).negation == res.get(index).negation) {
							for (int jj = 0; jj < res.get(index).values.size(); jj++) {
								if (res.get(index).values.get(jj) == generated.get(i).get(j).values.get(jj)) {
									continue;
								} else
									return false;
							}
						} else
							return false;
					} else {
						return false;
					}
				}
			}
		}
		return true;
	}

	boolean check_Predicate(ArrayList<Predicate> res) {
		for (int j = 0; j < generated.size(); j++) {
			if (generated.get(j).size() == res.size()) {
				int i = 0;
				for (int jj = 0; jj < generated.get(j).size(); jj++) {
					if (generated.get(j).get(jj).predicate.equals(res.get(i).predicate)) {
						if (compare_objects(generated.get(j).get(jj), res.get(i))) {
							i++;
							continue;
						} else
							break;
					}
				}
				if (i == res.size())
					return true;
			}
		}
		return false;
	}

	boolean compare_objects(Predicate gen, Predicate res) {
		if (gen.predicate.equals(res.predicate)) {
			if (gen.negation == res.negation) {
				for (int i = 0; i < gen.values.size(); i++) {
					if (gen.values.get(i).equals(res.values.get(i))) {
						continue;
					} else
						return false;
				}
			} else
				return false;
		} else
			return false;

		return true;
	}

	public HashMap<String, String> Unify(Object x, Object y, HashMap<String, String> res) {
		if (res == null)
			return null;
		else if (x.equals(y))
			return res;
		else if (isvariable(x)) {
			return (unify_Var((String) x, (String) y, res));
		} else if (isvariable(y)) {
			return (unify_Var((String) y, (String) x, res));
		} else if ((x instanceof ArrayList<?>) && (y instanceof ArrayList<?>)) {
			ArrayList<String> val1 = (ArrayList<String>) x;
			ArrayList<String> val2 = (ArrayList<String>) y;
			if (val1.size() != val2.size())
				return null;
			String temp1 = null, temp2 = null;
			if (val1.size() > 0) {
				temp1 = val1.get(0);
			} else {
				temp1 = null;
			}

			if (val2.size() > 0) {
				temp2 = val2.get(0);
			} else {
				temp2 = null;
			}
			ArrayList<String> copy1 = (ArrayList<String>) val1.clone();
			ArrayList<String> copy2 = (ArrayList<String>) val2.clone();
			if (copy1.size() > 0 && copy2.size() > 0) {
				copy1.remove(0);
				copy2.remove(0);
			}
			return Unify(copy1, copy2, Unify(temp1, temp2, res));
		} else
			return null;
	}


	boolean isvariable(Object x) {
		if (!(x instanceof String)) {
			return false;
		}
		String s = (String) x;
		if (Character.isLowerCase(s.charAt(0)))
			return true;
		else
			return false;

	}

	HashMap<String,String> unify_Var(String var, String x, HashMap<String, String> res)
	{
       if(res.containsKey(var))
    	   return Unify(res.get(var), x, res);
       else if(res.containsKey(x))
    	   return Unify(x, res.get(x), res);
       else
       {
    	   res.put(var,x);
    	   return res;
       }
	}

	// generate the intermediate Knowledge base based on the query
	boolean generate_Intermediate_KB(ArrayList<Predicate> que) {
		for (int i = 0; i < que.size(); i++)
		{
			for (int j = 0; j < kb.size(); j++)
			{
				for (int index = 0; index < kb.get(j).size(); index++)
				{
					if (kb.get(j).get(index).predicate.equals(que.get(i).predicate))
					{
						if (kb.get(j).get(index).negation != que.get(i).negation)
						{
							ArrayList<Predicate> que_temp = deepcopy(que);
							HashMap<String, String> replace= new HashMap<String,String>();
							replace = Unify(kb.get(j).get(index).values,que.get(i).values , replace);
							ArrayList<Predicate> res= new ArrayList<Predicate>();
							if(replace==null)
							{
							   res=checkingconstants(j,index,replace,que_temp,i);
							}
							else
							{
							  res=unification(j,index,replace,que_temp,i);
							}
							if(res!=null)
							{
								if(res.size()==0)
									return true;

								if (!check_Predicate(res))
								{
									//System.out.println("************");
									//System.out.println(j+1);
									//System.out.println(replace);
									//printsinglepredicate(que);
									///printsinglepredicate(kb.get(j));
									//printsinglepredicate(res);
									intermediatae.add(res);
									generated.add(res);
									//System.out.println("************");
								}
							}
						}
					}
				}
			}
		}
		return false;
	}


	ArrayList<Predicate> checkingconstants(int i, int index, HashMap<String, String> subs, ArrayList<Predicate> que,int que_index)
	{
		ArrayList<Predicate> replace = deepcopy(kb.get(i));
		int count=0;
		for(int z=0;z<replace.get(index).values.size();z++)
		{
			if(replace.get(index).values.get(z).equals(que.get(que_index).values.get(z)))
			{
				count++;
				continue;
			}
			else
				break;
		}
		if(count==replace.get(index).values.size())
		{
			que.remove(que_index);
			replace.remove(index);
			for (int qq = 0; qq < que.size(); qq++)
			{
				ArrayList<String> values = new ArrayList<String>();
				for (int qqq = 0; qqq < que.get(qq).values.size(); qqq++) {
					values.add(que.get(qq).values.get(qqq));
				}
				Predicate p = new Predicate(que.get(qq).predicate, que.get(qq).negation, values);
				replace.add(p);
			}
		   return replace;
		}
		return null;
	}


	// Unification
	ArrayList<Predicate> unification(int i, int index, HashMap<String, String> subs, ArrayList<Predicate> que,
			int que_index) {
		ArrayList<Predicate> replace = deepcopy(kb.get(i));

		que.remove(que_index);
		replace.remove(index);
		for (int qq = 0; qq < que.size(); qq++) {
			ArrayList<String> values = new ArrayList<String>();
			for (int qqq = 0; qqq < que.get(qq).values.size(); qqq++) {
				values.add(que.get(qq).values.get(qqq));
			}
			Predicate p = new Predicate(que.get(qq).predicate, que.get(qq).negation, values);
			replace.add(p);
		}
		for (int j = 0; j < replace.size(); j++) {
			for (int jj = 0; jj < replace.get(j).values.size(); jj++) {
				if (subs.containsKey(replace.get(j).values.get(jj))) {
					String val = subs.get(replace.get(j).values.get(jj));
					replace.get(j).values.remove(jj);
					replace.get(j).values.add(jj, val);
				}
			}
		}
		ArrayList<Predicate> replace_temp=deepcopy(replace);
		ArrayList<Integer> locations= new ArrayList<Integer>();
		for(int index1=0;index1<replace_temp.size();index1++)
		{
			for(int index2=0;index2<replace_temp.size();index2++)
			{
				if(index1!=index2 && replace_temp.get(index1).predicate.equals(replace_temp.get(index2).predicate) &&
						replace_temp.get(index1).negation==(replace_temp.get(index2).negation) && val_values(replace_temp.get(index1).values,replace_temp.get(index2).values))
				{
					locations.add(index1);
				}
			}
		}
		replace.clear();
		for(int vv=0;vv<replace_temp.size();vv++)
		{
			if(!locations.contains(vv))
			{
				replace.add(replace_temp.get(vv));
			}
		}
		return replace;
	}


	boolean val_values(ArrayList<String> val1, ArrayList<String> val2)
	{
		int count=0;
		for(int i=0;i<val1.size();i++)
		{
			if(val1.equals(val2))
				count++;
			else
				break;
		}
		if(count==val1.size())
			return true;
		return false;
	}

	boolean iterate(long max_time) {
		while (true) {
			if(System.currentTimeMillis()>max_time)
			{
				return false;
			}
			ArrayList<ArrayList<Predicate>> inter = complete_DeepCopy(intermediatae);
			intermediatae.clear();
			for (int i = 0; i < inter.size(); i++) {
				boolean result = generate_Intermediate_KB(inter.get(i));
				if (result) {
					return true;
				}
			}
			if (intermediatae.size() == 0) {
				return false;
			}
		}
	}

	// generating intermediate KB'S
	void generate_sentences() {
		ArrayList<String> result= new ArrayList<String>();
		int sz = 0;
		while (sz < query.size()) {
			ArrayList<Predicate> query_sentence = query.get(sz);
			if (query_sentence.get(0).negation == true)
				query_sentence.get(0).negation = false;
			else
				query_sentence.get(0).negation = true;

			kb.add(query_sentence);
			if (!generate_Intermediate_KB(query_sentence)) {
				long maxtimeallowed=System.currentTimeMillis()+60*1000;
				if (iterate(maxtimeallowed)) {
					//System.out.println("TRUE");
					result.add(sz,"TRUE");
				} else {
					//System.out.println("FALSE");
					//printobject(generated);
					result.add(sz,"FALSE");
				}
			} else {
				//System.out.println("TRUE");
				result.add(sz,"TRUE");
			}
			kb.remove(kb.size()-1);
			generated.clear();
			intermediatae.clear();
			sz++;
		}
		output_Generator(result);

	}

	// Standazing the KB
	void standardise_KB(ArrayList<ArrayList<Predicate>> kb) {
		for (int i = 0; i < kb.size(); i++) {
			for (int va = 0; va < kb.get(i).size(); va++) {
				for (int index = 0; index < kb.get(i).get(va).values.size(); index++)
				{
					if(isvariable(kb.get(i).get(va).values.get(index)))
					{
					String arval = kb.get(i).get(va).values.get(index);
					kb.get(i).get(va).values.remove(index);
					arval = arval + String.valueOf(i);
					kb.get(i).get(va).values.add(index, arval);
					}
				}
			}
		}
	}


	// input mapping to objects
	ArrayList<Predicate> Chaging_Input_To_Predicate(String algo)
	{
		ArrayList<Predicate> result = new ArrayList<Predicate>();
		String[] val = algo.split("\\|");
		Predicate p = null;
		String[] pv = null;
		String[] res = null;
		for (int i = 0; i < val.length; i++) {
			boolean negation = false;
			ArrayList<String> values = new ArrayList<String>();
			res = val[i].trim().split("\\(");
			pv = res[1].substring(0, res[1].length() - 1).trim().split("\\,");
			Collections.addAll(values, pv);
			if (res[0].charAt(0) == '~') {
				negation = true;
				res[0] = res[0].substring(1, res[0].length());
			}
			p = new Predicate(res[0], negation, values);
			result.add(p);
		}
		return result;
	}

	public static void main(String args[]) throws FileNotFoundException, IOException {
		int v;
		 FOL hw = new FOL();
		BufferedReader br = null;
		FileReader fr = null;
		StringBuffer sb = new StringBuffer();
		File myFile = new File("input.txt");
		fr = new FileReader(myFile.getCanonicalPath());
		br = new BufferedReader(fr);
		algo = br.readLine();
		inputs = Integer.parseInt(algo);
		int n = inputs;
		while (n > 0) {
			algo = br.readLine();
			query.add(hw.Chaging_Input_To_Predicate(algo));
			n--;
		}
		//hw.printobject(query);
		algo = br.readLine();
		kbsize = Integer.parseInt(algo);
		int m = kbsize;
		while (m > 0) {
			algo = br.readLine();
			kb.add(hw.Chaging_Input_To_Predicate(algo));
			m--;
		}
		//hw.printobject(kb);
		hw.standardise_KB(kb);
		hw.generate_sentences();
	}

	ArrayList<Predicate> deepcopy(ArrayList<Predicate> que) {
		ArrayList<Predicate> que_temp = new ArrayList<Predicate>();
		for (int ii = 0; ii < que.size(); ii++) {
			ArrayList<String> values = new ArrayList<String>();
			for (int jj = 0; jj < que.get(ii).values.size(); jj++) {
				values.add(que.get(ii).values.get(jj));
			}
			Predicate p = new Predicate(que.get(ii).predicate, que.get(ii).negation, values);
			que_temp.add(p);
		}
		return que_temp;

	}

	static void output_Generator(ArrayList<String> value) {
		BufferedWriter br = null;
		try {

				  br = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("output.txt")));
	              for(int i=0;i<value.size();i++)
		              {
		                   	  br.append(value.get(i));
		                   	  br.newLine();
		              }
				  br.close();
	         }

            catch (IOException ex) {

		}
	}


	ArrayList<ArrayList<Predicate>> complete_DeepCopy(ArrayList<ArrayList<Predicate>> intermediate)
	{
		ArrayList<ArrayList<Predicate>> res = new ArrayList<ArrayList<Predicate>>();
		for (int i = 0; i < intermediate.size(); i++) {
			ArrayList<Predicate> temp = new ArrayList<Predicate>();
			for (int ii = 0; ii < intermediate.get(i).size(); ii++)
			{
				ArrayList<String> values = new ArrayList<String>();
				for (int jj = 0; jj < intermediate.get(i).get(ii).values.size(); jj++)
				{
					values.add(intermediate.get(i).get(ii).values.get(jj));
				}
				Predicate p = new Predicate(intermediate.get(i).get(ii).predicate, intermediate.get(i).get(ii).negation,values);
				temp.add(p);
			}
			res.add(temp);
		}
		return res;
	}

}
