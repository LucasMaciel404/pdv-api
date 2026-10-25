le
package com.lucasmaciel404.pdv_api.dto.mapper;

import com.lucasmaciel404.pdv_api.dto.response.TableResponse;
import com.lucasmaciel404.pdv_api.model.TableModel;
import org.springframework.stereotype.Component;

@Component
public class TableMapper {

    public TableResponse toResponse(TableModel table) {
        return new TableResponse(
                table.getId(),
                table.getNumber(),
                table.getStatus(),
                table.getCreatedAt(),
                table.getEstablishment().getId(),
                table.getCard() != null
                        ? table.getCard().getId()
                        : null,
                table.getActive()
        );
    }
}
