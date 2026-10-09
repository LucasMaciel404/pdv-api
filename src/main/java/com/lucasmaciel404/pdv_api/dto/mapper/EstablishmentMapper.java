package com.lucasmaciel404.pdv_api.dto.mapper;
import com.lucasmaciel404.pdv_api.dto.response.AddUserToEstablishmentResponse;
import com.lucasmaciel404.pdv_api.dto.response.CreateEstablishmentResponse;
import com.lucasmaciel404.pdv_api.dto.response.FindAllEstablishmentsResponse;
import com.lucasmaciel404.pdv_api.dto.response.FindEstablishmentByIdResponse;
import com.lucasmaciel404.pdv_api.dto.response.FindEstablishmentsByUserResponse;
import com.lucasmaciel404.pdv_api.dto.response.UpdateEstablishmentResponse;
import com.lucasmaciel404.pdv_api.model.Establishment;
import com.lucasmaciel404.pdv_api.model.UserEstablishment;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EstablishmentMapper {

    public CreateEstablishmentResponse toCreateResponse(
            Establishment establishment
    ) {
        return new CreateEstablishmentResponse(
                establishment.getId(),
                establishment.getName(),
                establishment.getActive(),
                establishment.getCreatedAt()
        );
    }

    public AddUserToEstablishmentResponse toAddUserResponse(
            UserEstablishment userEstablishment
    ) {
        return new AddUserToEstablishmentResponse(
                userEstablishment.getId(),
                userEstablishment.getUser().getId(),
                userEstablishment.getEstablishment().getId(),
                userEstablishment.getRole()
        );
    }

    public FindAllEstablishmentsResponse toFindAllResponse(
            List<Establishment> establishments
    ) {
        List<CreateEstablishmentResponse> responses = establishments.stream()
                .map(this::toCreateResponse)
                .toList();

        return new FindAllEstablishmentsResponse(responses);
    }

    public FindEstablishmentByIdResponse toFindByIdResponse(
            Establishment establishment
    ) {
        return new FindEstablishmentByIdResponse(
                establishment.getId(),
                establishment.getName(),
                establishment.getActive(),
                establishment.getCreatedAt()
        );
    }

    public FindEstablishmentsByUserResponse toFindByUserResponse(
            Establishment establishment
    ) {
        return new FindEstablishmentsByUserResponse(
                establishment.getId(),
                establishment.getName(),
                establishment.getActive(),
                establishment.getCreatedAt()
        );
    }

    public UpdateEstablishmentResponse toUpdateResponse(
            Establishment establishment
    ) {
        return new UpdateEstablishmentResponse(
                establishment.getId(),
                establishment.getName(),
                establishment.getActive(),
                establishment.getCreatedAt()
        );
    }
}