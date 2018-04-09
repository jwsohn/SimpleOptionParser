#!/usr/bin/env python3
import sys

class SimpleOptionParser(object):
    """Parses command line args and options into dict and list"""

    def __init__(self):
        self.option_value_dict = {}
        self.arg_list = []
        self.usage = ""

        self.equivalent_option_dict = {}

        self.no_parameter_option_list = []
        self.single_parameter_option_list = []

        self.TRUE = 'TRUE'

    def put_no_parameter_option(self, no_parameter_option):
        self.option_value_dict[no_parameter_option] = None
        self.no_parameter_option_list.append(no_parameter_option)

    def put_single_parameter_option(self, single_parameter_option):
        """Declare -f as an option with a single parameter, for example"""
        self.option_value_dict[single_parameter_option] = None
        self.single_parameter_option_list.append(single_parameter_option)

    def store_equivalent_options(self, option, equivalent):
        """Make -f and --foobar be the same options, for example"""
        # NOTE key: '--foobar' and value: '-f'. Note the reversed order
        self.equivalent_option_dict[equivalent] = option
    
    def add_usage_line(self, line):
        self.usage = self.usage + line + '\n'

    def parse(self, input_arg_list):
        """
        Parses input_arg_list into option_value_dict, equivalent_option_dict

        Returns:
            True if parsing was successful. False if not
        """
        if (len(input_arg_list) == 1):
            print(self.usage)

            return True

        iterator = iter(input_arg_list)
        next(iterator)      # pass the command

        while True:
            try: 
                arg = next(iterator)
            except StopIteration:
                break       # break out of while
            else:
                # Prepare option from arg as the key for the option_value_dict
                option = None
                if arg.startswith('--'):
                    option = self.equivalent_option_dict[arg]

                    # if invalid option starting with --, let if-elses below
                    # generate error
                    if option is None:
                        option = arg
                elif arg.startswith('-'):
                    option = arg
                else:
                    option = None

                # NOTE option has the key, and arg has the original user input 
                if option != None:
                    if option in self.no_parameter_option_list:
                        if self.option_value_dict[option] != None:
                            print("Error: duplicate entry for option", arg)

                            return False

                        self.option_value_dict[option] = self.TRUE
                    elif option in self.single_parameter_option_list:
                        try:
                            value = next(iterator)
                        except StopIteration:
                            print("Error: option", arg, "has no parameter.")
                            
                            return False
                        else: 
                            if value.startswith('-'):
                                print("Error: option", arg,
                                        "does not have corresponding parameter.")

                                return False
                            
                            if self.option_value_dict[option] != None:
                                print("Error: duplicate entry for option", arg)

                                return False

                            # if all conditions are passed
                            self.option_value_dict[option] = value
                    else:
                        print("Error: invalid option", arg)                        

                        return False
                        
                else:   # if arg is not option
                    self.arg_list.append(arg)

        if len(self.arg_list) == 0: 
            print("Error: no arguments entered.")

            return False

        return True
        

"""Example Code"""
if __name__=='__main__':
    p = SimpleOptionParser()

    """Prepare for parsing"""
    p.add_usage_line("Usage: application [options] ... [files] ...")
    p.add_usage_line("\t-t , --toggle           No parameter option example")
    p.add_usage_line("\t-f , --foobar [number]  Single parameter option example")

    p.put_single_parameter_option('-t')
    p.store_equivalent_options('-t', '--toggle')

    p.put_single_parameter_option('-f')
    p.store_equivalent_options('-f', '--foobar')

    """Parsing"""
    p.parse(sys.argv)

    """Results"""
    print()
    print("# No parameter option list")
    print(p.no_parameter_option_list)
    print("# Single parameter option list")
    print(p.single_parameter_option_list)

    print()

    print("# option_value_dict")
    print(p.option_value_dict)
    print("# equivalent_option_dict")
    print(p.equivalent_option_dict)
    print("# arg_list")
    print(p.arg_list)
    print("# usage")
    print(p.usage)

