package com.siged.script.mail.inboundmail;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.siged.model.InboundMailModel;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;

import org.alfresco.service.namespace.QName;

import org.json.JSONObject;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;

public class PutInboundMail extends DeclarativeWebScript {

    private NodeService nodeService;

    @Override
    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status) {

        Map<String, Object> model = new HashMap<String, Object>();

        // Map<String, String> authorities = new LinkedHashMap<String, String>();
        String inboundMailReceivedDate="", inboundMailPriority="", inboundMailObject="", inboundexpediteur="",
                inboundMailObservation="", mailNature="", creationSiteId="", inboundMailOriginRef="", recipientGroup="";

        Map<String, String> params = req.getServiceMatch().getTemplateVars();

        String mailId = params.get("mailId");

        if ("".equals(mailId) || mailId == null) {
            status.setCode(400);
            status.setMessage("param mailId is undefined!");
            status.setRedirect(true);
            return model;
        }
        NodeRef mailNodeRef = new NodeRef("workspace://SpacesStore/" + mailId);

        try {

            JSONObject jsonParams = (JSONObject) req.parseContent();

            inboundMailPriority = jsonParams.getString("acme:inboundMailPriority");
            inboundMailObject = jsonParams.getString("acme:inboundMailObject");
            inboundexpediteur = jsonParams.getString("acme:inboundexpediteur");
            // haveAttachments = jsonParams.getString("acme:haveAttachments");
            inboundMailObservation = jsonParams.getString("acme:inboundMailObservation");
            mailNature = jsonParams.getString("acme:mailNature");
            creationSiteId = jsonParams.getString("acme:creationSiteId");
            // mailState = jsonParams.getString("acme:mailState");
            // mailreference = jsonParams.getString("acme:mailreference");
            inboundMailOriginRef = jsonParams.getString("acme:inboundMailOriginRef");
            recipientGroup = jsonParams.getString("acme:recipientGroup");
            inboundMailReceivedDate = jsonParams.getString("acme:inboundMailReceivedDate");

        } catch (Exception E) {
/*             status.setCode(400);
            status.setMessage("mail parameters are undefined!");
            status.setRedirect(true);
            return model; */
        }

        if ("".equals(inboundMailPriority) || inboundMailPriority == null) {
            status.setCode(400);
            status.setMessage("La priorité du courrier n'est pas définie!");
            status.setRedirect(true);
            return model;
        }
        if ("".equals(inboundMailObject) || inboundMailObject == null) {
            status.setCode(400);
            status.setMessage("L'objet du courrier n'est pas défini!");
            status.setRedirect(true);
            return model;
        }
        if (inboundMailObject.length() > 255) {
            status.setCode(400);
            status.setMessage("L'objet doit contenir au plus 255 caractères!");
            status.setRedirect(true);
            return model;
        }
        if ("".equals(inboundexpediteur) || inboundexpediteur == null) {
            status.setCode(400);
            status.setMessage("L'expéditeur du courrier n'est pas défini!");
            status.setRedirect(true);
            return model;
        }
        if ("".equals(mailNature) || mailNature == null) {
            status.setCode(400);
            status.setMessage("La nature du courrier n'est pas définie!");
            status.setRedirect(true);
            return model;
        }
        if ("".equals(inboundMailOriginRef) || inboundMailOriginRef == null) {
            status.setCode(400);
            status.setMessage("La référence d'origine courrier n'est pas définie!");
            status.setRedirect(true);
            return model;
        }
        if ("".equals(recipientGroup) || recipientGroup == null) {
            status.setCode(400);
            status.setMessage("Le destinataire du courrier n'est pas définie!");
            status.setRedirect(true);
            return model;
        }
        if ("".equals(inboundMailReceivedDate) || inboundMailReceivedDate == null) {
            status.setCode(400);
            status.setMessage("La date de reception du courrier n'est pas définie!");
            status.setRedirect(true);
            return model;
        }

        Map<QName, Serializable> mailProperties = nodeService.getProperties(mailNodeRef);

        mailProperties.put(InboundMailModel.QNAME_ORIGIN_REF, inboundMailOriginRef);
        mailProperties.put(InboundMailModel.QNAME_NATURE, mailNature);
        mailProperties.put(InboundMailModel.QNAME_PRIORITY, inboundMailPriority);
        mailProperties.put(InboundMailModel.QNAME_SENDER, inboundexpediteur);

        try {
            Date rdate = new SimpleDateFormat("yyyy-MM-dd").parse(inboundMailReceivedDate);
            mailProperties.put(InboundMailModel.QNAME_RECEIVED_DATE,rdate);
        } catch (ParseException e) {
            status.setCode(400);
            status.setMessage("Erreur sur le format de la date de reception");
            status.setRedirect(true);
            return model;
        }

        mailProperties.put(InboundMailModel.QNAME_SUBJECT,inboundMailObject);
        mailProperties.put(InboundMailModel.QNAME_RECIPIENT_GROUP,recipientGroup);

        if(creationSiteId != null && !"".equals(creationSiteId))
          mailProperties.put(InboundMailModel.QNAME_CREATION_SITE_ID,creationSiteId);

        if(inboundMailObservation != null && !"".equals(inboundMailObservation))
          mailProperties.put(InboundMailModel.QNAME_OBSERVATION,inboundMailObservation);


        nodeService.setProperties(mailNodeRef, mailProperties);

        model.put("message", "Courrier enregistré avec succès!");
        return model;
	}

	public NodeService getNodeService() {
		return nodeService;
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService =  nodeService;
	}
}
