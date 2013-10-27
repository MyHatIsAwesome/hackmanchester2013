package clockwork;

import clockwork.exception.InsufficiantBalanceException;
import clockwork.exception.InvalidFieldException;
import com.clockworksms.ClockworkException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * //TODO Comment!
 * @author BuckleWoods (Nathan Buckley, Andrew Isherwood, Joe Westwood)
 */
public class Controller 
{
    private Model   model;
    /**
     * Thread that every 5 minutes will 
     * call cullPhones to remove inactive 
     * phones. 
     */
    private Culler  culler;
    private static final Logger LOG = Logger.getLogger("MainLogger");
    
    /**
     * Controller Constructor that takes a Model instance that it will use.
     * @param model - the model that the Controller will use. 
     */
    public Controller(Model model)
    {        
        if(model == null)
        {
            //TODO Handle this!
        }
        
        this.model = model;
        
        culler = new Culler();  
        culler.start();
    }
    
    /**
     * Takes a socket. Gets data from it which should be sent in a particular
     * format, initilises or grabs existing Phone object that should be associated
     * with the message. Then closes the socket.
     * 
     * //TODO tidy up
     * @param incoming 
     */
    public void acceptIncoming(Socket socket) throws IOException, InvalidFieldException, InsufficiantBalanceException
    {
        if(socket == null)
        {
            LOG.log(Level.INFO, "Incoming socket is null. Throwing IOException");
            throw new IOException("Incoming Socket null. ");
        }
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String inLine; 
        while((inLine = in.readLine())!=null)
        {
            LOG.log(Level.INFO, "Message In: " + inLine);
            Message msg = createNewMessage(inLine);
            handleMessage(msg);                 
        }
    }
    
    private Message createNewMessage(String incomingMsg) throws InvalidFieldException
    {
        if(incomingMsg == null)
        {
            throw new InvalidFieldException("Controller.createNewMessage incomingMsg parameter is null.");
        }
        
        String number   = null;
        String to       = null;
        String msg      = null;
        String msgId   = null;
        
        String[] fields = incomingMsg.split("&");

        for(String field : fields)
        {
            String[] pair = field.split("=");
            if(pair.length == 2)
            {
                if(pair[0].toLowerCase().equals("from"))
                {
                    number = pair[1];
                }
                else if(pair[0].toLowerCase().equals("to"))
                {
                    to = pair[1];
                }
                else if(pair[0].toLowerCase().equals("msg"))
                {
                    msg = pair[1];
                }
                else if(pair[0].toLowerCase().equals("msg_id"))
                {
                    msgId = pair[1];
                }
            }
        }       
        
        return new Message(number, to, msg, msgId);
    }    
    
    private void handleMessage(Message msg) throws InsufficiantBalanceException
    {
        //Create/Get Phone
        Phone phone = model.getPhone(msg.getNumber());
                        
        //Send Message from phone to ai, and then response to phone.
        phone.sendMessage(model.getKey(), msg);
                
    }
    
    /**
     * Applies a lock on Phones member, and goes through all phones to see
     * if they have timedOut. Then unlocks. 
     */
    private synchronized void cullPhones()
    {
        LOG.log(Level.INFO, "Culling Inactive Phones");
        //TODO Comment
        ArrayList<String>      phonesToRemove = new ArrayList<>();
        
        //TODO Lock
        HashMap<String, Phone> phones = model.getPhones();
        
        //If no phones then just return.
        if(phones.size() <= 0)
        {
            return;
        }
        
        Phone[] phonesArray = phones.values().toArray(new Phone[0]);
        
        if(phonesArray!=null)
        {
            for(Phone phone : phonesArray)
            {
                if(phone.timedOut(model.getMaxTimeout()))
                {
                    LOG.log(Level.INFO, "Phone: " + phone.getNumber() + "Timed Out. Removing");
                    phonesToRemove.add(phone.getNumber());
                }
            }
        }
        else
        {
            return;             
        }
        
        for(String number : phonesToRemove)
        {
            Phone phone = phones.remove(number);
            try{
                phone.bye(model.getKey(), "This ends the conversation");
            }
            catch(InsufficiantBalanceException excep)
            {
                LOG.log(Level.WARNING, "Insufficiant funds to send Bye SMS.");
            }
            catch(ClockworkException excep)
            {
                //Log exception, but no then continue.
                LOG.log(Level.SEVERE, "Tried to say bye to phone[" + phone.getNumber() + "] threw exception.", excep);
            }
        }
    }
    
    class Culler extends Thread
    {
        private boolean running = false;
        private long    lastCull;
        @Override
        public void run() 
        {
            running = true;
            lastCull = System.currentTimeMillis();
            while(running)
            {                
                try
                {
                    Thread.sleep(300000l);
                }
                catch(InterruptedException excep)
                {
                    
                }
                cullPhones();
            }
        }
        
    }
}
