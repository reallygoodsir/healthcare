package com.really.good.sir.resources;

import com.really.good.sir.converter.ServiceConverter;
import com.really.good.sir.dao.ServiceDAO;
import com.really.good.sir.dto.ServiceDTO;
import com.really.good.sir.entity.ServiceEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/services")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ServiceResource {
    private static final Logger LOGGER = LogManager.getLogger(ServiceResource.class);

    private final ServiceDAO serviceDAO = new ServiceDAO();
    private final ServiceConverter serviceConverter = new ServiceConverter();

    @GET
    public Response getAllServices() {
        List<ServiceEntity> entities = serviceDAO.getAllServices();
        List<ServiceDTO> dtos = serviceConverter.convert(entities);
        return Response.ok(dtos).build();
    }

    @GET
    @Path("/{id}")
    public Response getServiceById(@PathParam("id") int id) {
        ServiceEntity entity = serviceDAO.getServiceById(id);
        if (entity == null) return Response.status(Response.Status.NOT_FOUND).build();
        return Response.ok(serviceConverter.convert(entity)).build();
    }

    @POST
    public Response createService(ServiceDTO dto) {
        ServiceEntity entity = serviceConverter.convert(dto);
        ServiceEntity created = serviceDAO.createService(entity);
        return Response.ok(serviceConverter.convert(created)).build();
    }

    @PUT
    public Response updateService(ServiceDTO dto) {
        ServiceEntity entity = serviceConverter.convert(dto);
        boolean updated = serviceDAO.updateService(entity);
        LOGGER.info("Service updated: {}", updated);
        return Response.ok(serviceConverter.convert(entity)).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteService(@PathParam("id") int id) {
        boolean deleted = serviceDAO.deleteService(id);
        LOGGER.info("Service deleted: {}", deleted);
        return Response.noContent().build();
    }
}
