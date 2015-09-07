package com.simplegame.server.message.manager;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.simplegame.protocol.message.Message.DestType;
import com.simplegame.protocol.message.Message.FromType;
import com.simplegame.server.message.IMsgDispatcher;
import com.simplegame.server.message.MsgUtil;
import com.simplegame.server.share.moduleinit.CommandGroup;

/**
 * @Author zeusgooogle@gmail.com
 * @sine   2015年5月10日 下午8:41:22 
 *
 */
@Component
public class SwapManager {

    private Logger LOG = LoggerFactory.getLogger(SwapManager.class);
    
	@Resource
	private IMsgDispatcher ioDispatcher;
	
	@Resource
	private IMsgDispatcher publicDispatcher;
	
	@Resource
	private IMsgDispatcher stageDispatcher;
	
	public void swap(Object[] message) {
		FromType fromType = MsgUtil.getFromType(message);
		switch(fromType) {
		case CLIENT:
			swapClientMsg(message);
			break;
		case BUS:
			swapPublicMsg(message);
			break;
		case STAGE:
		    componentMsgSwap(message);
		    break;
		default:
			break;
		}
	}
	
	public void swapClientMsg(Object[] message) {
		String command = MsgUtil.getCommand(message);
		int group = CommandGroup.getCmdDest(command);
		
		//dest type to group
		message[2] = Integer.valueOf(group);
		
		LOG.info("swap msg command: {}, dest group: {}", command, group);
		
		switch(group) {
		case 1:
		case 3:
		    toStage(message);
		    break;
		case 4:
			toPublic(message);
			break;
		}
	}
	
	public void swapPublicMsg(Object[] message) {
		componentMsgSwap(message);
	}
	
	private void componentMsgSwap(Object[] message) {
		DestType destType = MsgUtil.getDestType(message);
		switch(destType) {
		case CLIENT:
			toClient(message);
			break;
		case BUS:
			break;
		case INOUT:
			break;
		case PUBLIC:
			break;
		case STAGE:
		    toStage(message);
			break;
		case INNER_SYSTEM:
			break;
		default:
			break;
		}
	}
	
	private void toClient(Object message) {
		ioDispatcher.in(message);
	}
	
	private void toPublic(Object message) {
		publicDispatcher.in(message);
	}
	
	private void toStage(Object message) {
	    stageDispatcher.in(message);
	}
}
