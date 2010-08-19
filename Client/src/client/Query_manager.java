package client;

import java.util.regex.Matcher;
import java.util.regex.Pattern;



/*
 * 쿼리를 해석하고 서버로 보내고, 결과값을 반환받아 프로그램으로 넘기는 Thread
 */

public class Query_manager{
   
    public void command_parser(String query)
    {
        String[] pattern = {"^(insert) key=\"([a-z]+)\" value=\"([a-z]+)\"","^(delete) key=\"([a-z]+)\"",
        "^(update) key=\"([a-z]+)\" value=\"([a-z]+)\"","^(search).key=\"([a-z]+)\""};

        for(int i=0;i<pattern.length;i++)
        {
            Pattern p = Pattern.compile(pattern[i]);
            Matcher m = p.matcher(query);
            while(m.find())
            {
                try
                {
                    System.out.println(m.group()+"->"+m.group(1)+", "+m.group(2)+", "+m.group(3));
                }
                catch(Exception e)
                {
                    System.out.println(m.group()+"->"+m.group(1)+", "+m.group(2));
                }
            }
        }
        
    }

    

}

