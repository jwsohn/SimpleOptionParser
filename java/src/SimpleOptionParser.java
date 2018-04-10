import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Parses command line args and options into {@literal optionValueMap}
 * and {@literal argList}.
 *
 * @author Jung-woo Sohn (jwsohn00@gmail.com)
 */

public class SimpleOptionParser 
{
    private Map<String, String> optionValueMap;
    private List<String> argList;
    private String usage;

    private Map<String, String> equivalentOptionMap;
    private final String TRUE = "TRUE";

    private List<String> noParameterOptionList;
    private List<String> singleParameterOptionList;
    
    
    public SimpleOptionParser()
    {
        optionValueMap = new HashMap<String, String>();
        equivalentOptionMap = new HashMap<String, String>();
        argList = new ArrayList<String>();
        usage = "";
        
        noParameterOptionList = new ArrayList<String>();
        singleParameterOptionList = new ArrayList<String>();
    }
    
    public void putNoParameterOption(String noParameterOption)
    {
        optionValueMap.put(noParameterOption, null);
        noParameterOptionList.add(noParameterOption);
    }
    
    /* Declare "-f" as an option with a single parameter, for example */
    public void putSingleParameterOption(String singleParameterOption)
    {
        optionValueMap.put(singleParameterOption, null);
        singleParameterOptionList.add(singleParameterOption);
    }
    
    /* Make "-f" and "--foobar" the same options, for example */
    public void storeEquivalentOptions(String option, String equivalent)
    {
        // key: "--foobar", value: "-f". Note the reversed order.
        equivalentOptionMap.put(equivalent, option);
    }
    
    public void addUsageLine(String line)
    {
        usage = usage + line + '\n';
    }
    
    public boolean parse(String[] args)
    {
        List<String> inputArgList = Arrays.asList(args);
        
        if (args.length == 0)
        {
            System.out.println(usage);
            return true;
        }
        
        Iterator<String> iterator = inputArgList.iterator();
        
        while (iterator.hasNext() == true)
        {
            String arg = iterator.next();
            
            /* prepare option from arg as the key for the optionValueMap */
            String option;
            if (arg.startsWith("--") == true)
            {
                option = equivalentOptionMap.get(arg);

                /* if invalid option starting with --, let if-elses below generate error */
                if (option == null)
                    option = arg;
            }
            else if (arg.startsWith("-") == true)
                option = arg;
            else 
                option = null;
            
            /* NOTE: option has the key, and arg has the original user input */
            if (option != null)
            {
                
                if (noParameterOptionList.contains(option) == true)
                {
                    if (optionValueMap.get(option) != null)
                    {
                        System.out.println("Error: duplicate entry for option " 
                                + arg);
                        
                        return false;
                    }
                    
                    optionValueMap.put(option, TRUE);
                }
                else if (singleParameterOptionList.contains(option) == true)
                {
                    if (iterator.hasNext() == false)
                    {
                        System.out.println("Error: option " + arg 
                                + " has no parameter.");
                        
                        return false;
                    }
                    
                    String value = iterator.next();
                    
                    if (value.startsWith("-") == true)
                    {
                        System.out.println("Error: option " + arg 
                                + " does not have corresponding parameter.");
                        
                        return false;
                    }
                    
                    if (optionValueMap.get(option) != null)
                    {
                        System.out.println("Error: duplicate entry for option " 
                                + arg);
                        
                        return false;
                    }
                    
                    optionValueMap.put(option, value);
                }
                else
                {
                    System.out.println("Error: invalid option " + arg);
                    
                    return false;
                }
            }
            else    // if arg is not option
            {
                argList.add(arg);
            }    
        }

        if (argList.size() == 0) 
        {
            System.out.println("Error: no arguments entered.");
            
            return false;
        }
        
        return true;
    }
    
    /* getters */
    
    public Map<String, String> getOptionValueMap()
    {
        return optionValueMap;
    }

    public Map<String, String> getEquivalentOptionMap()
    {
        return equivalentOptionMap;
    }

    public List<String> getArgList()
    {
        return argList;
    }

    public String getUsage()
    {
        return usage;
    }

    public List<String> getNoParameterOptionList()
    {
        return noParameterOptionList;
    }

    public List<String> getSingleParameterOptionList()
    {
        return singleParameterOptionList;
    }

    
    /**
     * Example code
     * 
     * @param args
     */
    public static void main(String[] args)
    {
        SimpleOptionParser p = new SimpleOptionParser();
        
        /* Prepare for parsing */
        p.addUsageLine("Usage: application [options] ... [files] ...");
        p.addUsageLine("\t-t , --toggle             No parameter option example");
        p.addUsageLine("\t-f , --foobar [number]    Single parameter option example");
        
        p.putNoParameterOption("-t");
        p.storeEquivalentOptions("-t", "--toggle");
        
        p.putSingleParameterOption("-f");
        p.storeEquivalentOptions("-f", "--foobar");
        
        /* Parse */
        p.parse(args);
   
        /* Results */
        System.out.println();
        System.out.println("# No parameter option list");
        System.out.println(p.getNoParameterOptionList());
        System.out.println("# Single parameter option list");
        System.out.println(p.getSingleParameterOptionList());
        
        System.out.println();
        
        System.out.println("# OptionValueMap");
        System.out.println(p.getOptionValueMap());
        System.out.println("# EquivalentOptionMap");
        System.out.println(p.getEquivalentOptionMap());
        System.out.println("# Arguments list");
        System.out.println(p.getArgList());
        System.out.println("# Usage");
        System.out.println(p.getUsage());
    }
}

