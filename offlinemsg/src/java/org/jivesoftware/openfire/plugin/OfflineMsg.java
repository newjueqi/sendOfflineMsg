/**
 * Copyright (C) 2004-2008 Jive Software. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jivesoftware.openfire.plugin;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;


import org.jivesoftware.database.SequenceManager;
import org.jivesoftware.openfire.PresenceManager;
import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.container.Plugin;
import org.jivesoftware.openfire.container.PluginManager;
import org.jivesoftware.openfire.interceptor.InterceptorManager;
import org.jivesoftware.openfire.interceptor.PacketInterceptor;
import org.jivesoftware.openfire.interceptor.PacketRejectedException;
import org.jivesoftware.openfire.session.Session;
import org.jivesoftware.openfire.user.User;
import org.jivesoftware.openfire.user.UserManager;
import org.jivesoftware.openfire.user.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmpp.packet.IQ;
import org.xmpp.packet.JID;
import org.xmpp.packet.Message;
import org.xmpp.packet.Packet;
import org.xmpp.packet.Presence;
import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.container.Plugin;
import org.jivesoftware.openfire.container.PluginManager;
import org.jivesoftware.openfire.user.User;
import org.jivesoftware.openfire.user.UserManager;
import org.jivesoftware.openfire.PresenceManager;
import org.jivesoftware.util.JiveGlobals;
import org.jivesoftware.util.Log;


/**
 * <b>function:</b> send offline msg plugin
 * @author newjueqi
 * @createDate 2013-12-19 
 * @project OpenfirePlugin
 * @blog http://blog.csdn.net/newjueqi
 * @email h6k65@126.com
 * @version 1.0
 */
public class OfflineMsg implements PacketInterceptor, Plugin {

	private static final Logger log = LoggerFactory.getLogger(OfflineMsg.class);
	
    //Hook for intercpetorn
    private InterceptorManager interceptorManager;   
    private static PluginManager pluginManager;
    private UserManager userManager;
    private PresenceManager presenceManager;
    
    public OfflineMsg() {
        
    }
    
    public void debug(String str){
    	if( true ){
//    		System.out.println(str);
    	}
    }
    
    public void initializePlugin(PluginManager manager, File pluginDirectory) {
    	interceptorManager = InterceptorManager.getInstance();
        interceptorManager.addInterceptor(this);

        XMPPServer server = XMPPServer.getInstance();
        userManager = server.getUserManager();
        presenceManager = server.getPresenceManager();        
        
        pluginManager = manager;
        
        this.debug("start offline 1640");
    }

    public void destroyPlugin() {
    	 this.debug("start offline 1640");
    }

    /**
     * intercept message
     */
    @Override
    public void interceptPacket(Packet packet, Session session, boolean incoming, boolean processed) throws PacketRejectedException {


        JID recipient = packet.getTo();
        if (recipient != null) {

            String username = recipient.getNode();
            
            // if broadcast message or user is not exist
            if (username == null || !UserManager.getInstance().isRegisteredUser(recipient)) {

                return;

            } else if (!XMPPServer.getInstance().getServerInfo().getXMPPDomain().equals(recipient.getDomain())) {
            	//not from the same domain

                return;

            } else if ("".equals(recipient.getResource())) {

            }

        }

        this.doAction(packet, incoming, processed, session);

    } 
    
    
    /**
     * <b>send offline msg from this function </b>
     */
    private void doAction(Packet packet, boolean incoming, boolean processed, Session session) {

        Packet copyPacket = packet.createCopy();
        if (packet instanceof Message) {

            Message message = (Message) copyPacket;

            if (message.getType() == Message.Type.chat) {

                if (processed || !incoming) {
                    return;
                }

                Message sendmessage = (Message) packet;
                String content= sendmessage.getBody();
                JID recipient = sendmessage.getTo();
                
                //get message
                try
                {
                	
	                if (recipient.getNode() == null ||
	                        !UserManager.getInstance().isRegisteredUser(recipient.getNode())) {
	                    // Sender is requesting presence information of an anonymous user
	                    throw new UserNotFoundException("Username is null");
	                }
	                
	                Presence status=presenceManager.getPresence(userManager.getUser(recipient.getNode()));
	                
	                if( status!=null ){
	                	this.debug(recipient.getNode()+" online111"+",message: "+content);
	                }else{
	                	this.debug(recipient.getNode()+" offline111"+",message: "+content);
	                	
	                	/*
	                	 * add your code here to send offline msg
	                	 * recipient.getNode() : receive's id,for example,if  receive's jid is "23@localhost", receive's id is "23"
	                	 * content: message content
	                	 */
	                	
	                	
	                }//end if
	                
	            }
                catch (UserNotFoundException e) {
                	this.debug("exceptoin "+recipient.getNode()+" not find"+",full jid: "+recipient.toFullJID());
                }

            } else if (message.getType() ==  Message.Type.groupchat) {

                List<?> els = message.getElement().elements("x");
                if (els != null && !els.isEmpty()) {

                } else {
                }
            } else {

            }

        } else if (packet instanceof IQ) {

            IQ iq = (IQ) copyPacket;

            if (iq.getType() == IQ.Type.set && iq.getChildElement() != null && "session".equals(iq.getChildElement().getName())) {

            }

        } else if (packet instanceof Presence) {

            Presence presence = (Presence) copyPacket;

            if (presence.getType() == Presence.Type.unavailable) {


            }

        } 

    } 
    
}
