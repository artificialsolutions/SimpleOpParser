// Name: SimpleOpParser
// Version: 1.1.0
// Description: Helper class for Teneo Web Chat for easy message type creation in Teneo Studio.
// Homepage: https://developers.artificial-solutions.com/engine/teneo-web-chat/easy-message-type-creation-in-studio

public class SimpleOpParser{

    // QUICKREPLIES
    public static Map createQuickReplyItems(def quickReplies = ""){
        def quick_reply_items = []
		def quick_reply_map = [:]
        for(item in quickReplies.split("\\|")){
            quick_reply_items << ["title":item,"postback":item]
        }
		quick_reply_map.type = "quickreply"
		quick_reply_map.quick_replies = quick_reply_items

        return quick_reply_map
    }

    // BUTTONS
    public static Map createButtonItems(def buttons = ""){
        def button_items = []
        def button_map = [:]
        for(item in buttons.split("\\|").toList()){
            button_items << ["title":item,"postback":item]
        }
        button_map.type = "buttons"
        button_map.button_items = button_items

        return button_map
    }
    
    // LINKBUTTONS
    public static Map createLinkButtonItems(def buttons = ""){
        def linkbutton_items = []
        def linkbutton_map = [:]
        for(item in buttons.split("\\|").toList()){
            linkbutton_items << parseLinkButtonDetails(item)
        }
        linkbutton_map.type = "linkbuttons"
        linkbutton_map.linkbutton_items = linkbutton_items

        return linkbutton_map
    }

    // CLICKABLE LSITS
    public static Map createClickablelistItems(def clickablelist = ""){
        def clickablelist_items = []
        def clickablelist_map = [:]
        for(item in clickablelist.split("\\|").toList()){
            clickablelist_items << ["title":item,"postback":item]
        }
        clickablelist_map.type = "clickablelist"
        clickablelist_map.list_items = clickablelist_items

        return clickablelist_map
    }

    // MEDIA ATTACHMENTS
    public static Map createImageItem(def image = "") {
		def image_map = [:]
        if(image.contains("|")){
			def url = image.split("\\|")[0]
			def alt = image.split("\\|")[1]
			image_map = ['type':"image",'image_url':url,'alt':alt]
        } else {
			image_map = ['type':"image",'image_url':image]
		}
        
        return image_map
    }

    // AUDIO
    public static Map createAudioItem(def audioUrl = "") {
        def audio = ['type':"audio",'audio_url':audioUrl];
        return audio;
    }

    // VIDEO
    public static Map createVideoItem(def videoUrl = "") {
        def videoType = videoUrl.toLowerCase().contains("vimeo")?"vimeovideo":(videoUrl.toLowerCase().contains("youtube"))||(videoUrl.toLowerCase().contains("youtu.be"))?"youtubevideo":"filevideo";
        def video = ['type':videoType,'video_url':videoUrl];
        return video;
    }
	
    // SYSTEM MESSAGE
    public static Map createSystemMessageItem(def msg = "") {
        def system_msg = ['type':"system",'text':msg];
        return system_msg;
    }
    
	// TEXT BUBBLE
	public static Map createTextBubbleItem(def text = "") {
        def text_bubble = ['type':"text",'text':text];
        return text_bubble;
    }
	
    public static Map parseLinkButtonDetails(def buttonItem = "") {
    
        def buttonDetails = [:]
        
        if (buttonItem.split(",").length > 0) {
            buttonDetails.title = buttonItem.split(",")[0].trim()
        }
        if (buttonItem.split(",").length > 1) {
            buttonDetails.url = buttonItem.split(",")[1].trim()
        }
        if (buttonItem.split(",").length > 2) {
            buttonDetails.target = buttonItem.split(",")[2].trim()
        }
        
        return buttonDetails
    }
	
	// POSTPROCESSING PARSE PARAMS
	public static void parseParams(def _) {
        // Simple OP
        def lOpItemTypes = ["quickreply","buttons","linkbuttons","clickablelist","video","audio","image","system","text"]
        def lOpItems = []
        def lComboOrder = []
        def mModal = [:]
        def mCard = [:]
        def sClickableListTitle = ""
        def sButtonsTitle = ""
        def sLinkButtonsTitle = ""

        // Search for simple OPs and convert to JSON
        Iterator it = _.getOutputParameters().entrySet().iterator();
        while(it.hasNext()){
	
        	def entry = it.next();
        	if(entry.getKey() in lOpItemTypes){
		
        		switch(entry.getKey()){
			
        			case "quickreply":
        				lOpItems << SimpleOpParser.createQuickReplyItems(entry.getValue());
        				break;

        			case "buttons":
        				lOpItems << SimpleOpParser.createButtonItems(entry.getValue());
        				break;
                        
            		case "linkbuttons":
            			lOpItems << SimpleOpParser.createLinkButtonItems(entry.getValue());
            			break;

        			case "clickablelist":
        				lOpItems << SimpleOpParser.createClickablelistItems(entry.getValue());
        				break;

        			case "video":
        				lOpItems << SimpleOpParser.createVideoItem(entry.getValue());
        				break;
			
        			case "audio":
        				lOpItems << SimpleOpParser.createAudioItem(entry.getValue());
        				break;
				
        			case "image":
        				lOpItems << SimpleOpParser.createImageItem(entry.getValue());
        				break;
				
        			case "system":
        				lOpItems << SimpleOpParser.createSystemMessageItem(entry.getValue());
        				break;
				
        			case "text":
        				lOpItems << SimpleOpParser.createTextBubbleItem(entry.getValue());
        				break;

        		}
		
        		it.remove();
		
        	} else if (entry.getKey().startsWith("modal")){
		
        		if(!mModal.keySet().contains("type")) mModal.put("type","modal");
        		switch(entry.getKey()){
			
        			case "modal_title":
        				mModal.title = entry.getValue();
        				break;
				
        			case "modal_bodytext":
        				mModal.text = entry.getValue();
        				break;
				
        			case "modal_image":
        				def image_map = [:];
        				if(entry.getValue().contains("|")){
			
        					def url = entry.getValue().split("\\|")[0];
        					def alt = entry.getValue().split("\\|")[1];
        					image_map = ['type':"image",'image_url':url,'alt':alt];
			
        				} else {
			
        					image_map = ['type':"image",'image_url':entry.getValue()];
			
        				}
        				mModal.image = image_map;
        				break;
				
        			case "modal_buttons":
        				def lModalButtons = [];
        				for(item in entry.getValue().split("\\|")){
			
        					lModalButtons << ["title":item,"postback":item]
			
        				}
        				mModal.button_items = lModalButtons;
        				break;
				
        		}
		
        		it.remove();
		
        	} else if (entry.getKey().startsWith("card")){
		
        		if(!mCard.keySet().contains("type")) mCard.put("type","card");
        		switch(entry.getKey()){
			
        			case "card_title":
        				mCard.title = entry.getValue();
        				break;
				
        			case "card_subtitle":
        				mCard.subtitle = entry.getValue();
        				break;
				
        			case "card_bodytext":
        				mCard.text = entry.getValue();
        				break;
				
        			case "card_image":
        				def image_map = [:];
        				if(entry.getValue().contains("|")){
			
        					def url = entry.getValue().split("\\|")[0];
        					def alt = entry.getValue().split("\\|")[1];
        					image_map = ['type':"image",'image_url':url,'alt':alt];
			
        				} else {
			
        					image_map = ['type':"image",'image_url':entry.getValue()];
			
        				}
        				mCard.image = image_map;
        				break;
				
        			case "card_buttons":
        				def button_items = [];
        				for(item in entry.getValue().split("\\|")){
			
        					button_items << ["title":item,"postback":item]
			
        				}
        				mCard.button_items = button_items;
        				break;
				
        			case "card_clickablelist":
        				def clickablelist_items = []
        				for(item in entry.getValue().split("\\|")){
			
        					clickablelist_items << ["title":item,"postback":item]
			
        				}
        				mCard.list_items = clickablelist_items;
        				break;
				
        			case "card_links":
        				def link_items = []
        				for(item in entry.getValue().split("\\|")){
			
        					link_items << ["title":item.split(",")[0].trim(),"url":item.split(",")[1].trim()]
			
        				}
        				mCard.link_items = link_items;
        				break;
                    
        			case "card_linkbuttons":
        				def linkbutton_items = []
        				for(item in entry.getValue().split("\\|")){
		
        					linkbutton_items << parseLinkButtonDetails(item)
		
        				}
        				mCard.linkbutton_items = linkbutton_items;
        				break;
				
        		}
		
        		it.remove();
		
        	} else if (entry.getKey() == "combo_order"){
		
        		lComboOrder = entry.getValue().split("\\|");
        		it.remove();
		
        	} else if (entry.getKey() == "clickablelist_title"){
		
        		sClickableListTitle = entry.getValue();
        		it.remove();
		
        	} else if (entry.getKey() == "buttons_title"){
		
        		sButtonsTitle = entry.getValue();
        		it.remove();
		
        	} else if (entry.getKey() == "linkbuttons_title"){
		
        		sLinkButtonsTitle = entry.getValue();
        		it.remove();
		
        	}
	
        }

        // Add modal and card to lOpItems
        if(!mModal.isEmpty()) lOpItems << mModal;
        if(!mCard.isEmpty()) lOpItems << mCard;

        // Attach button title and list title to buttons and list
        if(sClickableListTitle||sButtonsTitle||sLinkButtonsTitle){
	
        	for(item in lOpItems){
		
        		if(item.type == "clickablelist"){
			
        			if(sClickableListTitle) item.title = sClickableListTitle;
			
        		} else if (item.type == "buttons"){
			
        			if(sButtonsTitle) item.title = sButtonsTitle;
			
        		} else if (item.type == "linkbuttons"){
			
        			if(sLinkButtonsTitle) item.title = sLinkButtonsTitle;
			
        		}
		
        	}
	
        }

        // Create JSON
        if(lOpItems.size() == 1){
            
        	def outputJson = new groovy.json.JsonBuilder(lOpItems[0]).toString();
        	_.putOutputParameter("teneowebclient",outputJson);

        } else if(lOpItems.size() > 1){

        	def lComboComponents = [];
        	if(lComboOrder.size() > 0){
		
        		for(type in lComboOrder){
        			for(item in lOpItems){
        				if(item.type.contains(type)) lComboComponents << item;
        			}
        		}
		
        	} else {
		
        		lComboComponents = lOpItems;
		
        	}
        	def outputJson = new groovy.json.JsonBuilder(["type":"combo","components":lComboComponents]).toString();
        	_.putOutputParameter("teneowebclient",outputJson);

        }
         
    }
	
}
