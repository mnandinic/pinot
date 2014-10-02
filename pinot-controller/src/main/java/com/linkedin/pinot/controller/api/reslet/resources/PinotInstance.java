package com.linkedin.pinot.controller.api.reslet.resources;

import org.apache.log4j.Logger;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.ByteStreams;
import com.linkedin.pinot.controller.ControllerConf;
import com.linkedin.pinot.controller.api.pojos.Instance;
import com.linkedin.pinot.controller.helix.core.PinotHelixResourceManager;
import com.linkedin.pinot.controller.helix.core.PinotResourceManagerResponse;

/**
 * @author Dhaval Patel<dpatel@linkedin.com>
 * Sep 30, 2014
 *
 */

public class PinotInstance extends ServerResource {

  private static final Logger logger = Logger.getLogger(PinotInstance.class);

  private final ControllerConf conf;
  private final PinotHelixResourceManager manager;
  private final ObjectMapper mapper;

  public PinotInstance() {
    getVariants().add(new Variant(MediaType.TEXT_PLAIN));
    getVariants().add(new Variant(MediaType.APPLICATION_JSON));
    setNegotiated(false);
    conf = (ControllerConf) getApplication().getContext().getAttributes().get(ControllerConf.class.toString());
    manager = (PinotHelixResourceManager) getApplication().getContext().getAttributes().get(PinotHelixResourceManager.class.toString());
    mapper = new ObjectMapper();
  }

  @Override
  @Post("json")
  public Representation post(Representation entity) {
    StringRepresentation presentation = null;
    try {
      final Instance instance = mapper.readValue(ByteStreams.toByteArray(entity.getStream()), Instance.class);
      final PinotResourceManagerResponse resp = manager.addInstance(instance);
      logger.info("instace create request recieved for instance : " + instance.toInstanceId());
      presentation = new StringRepresentation(resp.toJSON().toString());
    } catch (final Exception e) {
      presentation = new StringRepresentation(e.getMessage());
      logger.error(e);
    }
    return presentation;
  }

}
