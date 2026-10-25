package com.lucasmaciel404.pdv_api.service;

import com.lucasmaciel404.pdv_api.dto.enums.model.OrderStatusEnum;
import com.lucasmaciel404.pdv_api.dto.enums.model.TableStatusEnum;
import com.lucasmaciel404.pdv_api.dto.enums.model.UserRoleEnum;
import com.lucasmaciel404.pdv_api.model.CardModel;
import com.lucasmaciel404.pdv_api.model.Establishment;
import com.lucasmaciel404.pdv_api.model.TableModel;
import com.lucasmaciel404.pdv_api.model.UserEstablishment;
import com.lucasmaciel404.pdv_api.model.UserModel;
import com.lucasmaciel404.pdv_api.repository.CardRepository;
import com.lucasmaciel404.pdv_api.repository.EstablishmentRepository;
import com.lucasmaciel404.pdv_api.repository.OrderRepository;
import com.lucasmaciel404.pdv_api.repository.TableRepository;
import com.lucasmaciel404.pdv_api.repository.UserEstablishmentRepository;
import com.lucasmaciel404.pdv_api.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class TableService {

    private final TableRepository tableRepository;
    private final CardRepository cardRepository;
    private final EstablishmentRepository establishmentRepository;
    private final UserRepository userRepository;
    private final UserEstablishmentRepository userEstablishmentRepository;
    private final OrderRepository orderRepository;

    public TableService(
            TableRepository tableRepository,
            CardRepository cardRepository,
            EstablishmentRepository establishmentRepository,
            UserRepository userRepository,
            UserEstablishmentRepository userEstablishmentRepository,
            OrderRepository orderRepository
    ) {
        this.tableRepository = tableRepository;
        this.cardRepository = cardRepository;
        this.establishmentRepository = establishmentRepository;
        this.userRepository = userRepository;
        this.userEstablishmentRepository = userEstablishmentRepository;
        this.orderRepository = orderRepository;
    }

    private UserModel getAuthenticatedUser(String email) {
        UserModel user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Usuário autenticado não encontrado"
                ));

        if (!Boolean.TRUE.equals(user.getActive())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Usuário inativo"
            );
        }

        return user;
    }

    private UserEstablishment requireMembership(
            String email,
            UUID establishmentId
    ) {
        UserModel user = getAuthenticatedUser(email);

        return userEstablishmentRepository
                .findByUser_IdAndEstablishment_Id(
                        user.getId(),
                        establishmentId
                )
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "Você não possui acesso a este estabelecimento"
                ));
    }

    private void requireOwner(String email, UUID establishmentId) {
        UserEstablishment membership =
                requireMembership(email, establishmentId);

        if (membership.getRole() != UserRoleEnum.OWNER) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Somente o proprietário pode executar esta operação"
            );
        }

        if (!Boolean.TRUE.equals(
                membership.getEstablishment().getActive()
        )) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Estabelecimento inativo"
            );
        }
    }

    private TableModel getTableForUser(
            UUID tableId,
            String email
    ) {
        TableModel table = tableRepository.findById(tableId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Mesa não encontrada"
                ));

        requireMembership(
                email,
                table.getEstablishment().getId()
        );

        return table;
    }

    @Transactional
    public TableModel create(
            Integer number,
            UUID establishmentId,
            String email
    ) {
        requireOwner(email, establishmentId);

        if (number == null || number < 1) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "O número da mesa deve ser maior que zero"
            );
        }

        Establishment establishment = establishmentRepository
                .findById(establishmentId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Estabelecimento não encontrado"
                ));

        if (!Boolean.TRUE.equals(establishment.getActive())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Estabelecimento inativo"
            );
        }

        var existingTable = tableRepository
                .findByNumberAndEstablishmentId(number, establishmentId);

        if (existingTable.isPresent()) {
            TableModel table = existingTable.get();

            if (Boolean.TRUE.equals(table.getActive())) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Já existe uma mesa com esse número neste estabelecimento"
                );
            }

            table.setActive(true);

            return tableRepository.save(table);
        }

        TableModel table = new TableModel();
        table.setNumber(number);
        table.setEstablishment(establishment);
        table.setStatus(TableStatusEnum.OPEN);
        table.setActive(true);

        return tableRepository.save(table);
    }

    @Transactional
    public TableModel update(
            UUID tableId,
            Integer number,
            String email
    ) {
        TableModel table = getTableForUser(tableId, email);
        UUID establishmentId = table.getEstablishment().getId();

        requireOwner(email, establishmentId);

        if (number == null || number < 1) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "O número da mesa deve ser maior que zero"
            );
        }

        if (tableRepository.existsByNumberAndEstablishmentIdAndIdNot(
                number,
                establishmentId,
                tableId
        )) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Já existe outra mesa com esse número neste estabelecimento"
            );
        }

        table.setNumber(number);
        return tableRepository.save(table);
    }

    @Transactional
    public TableModel attachCard(
            UUID tableId,
            UUID cardId,
            String email
    ) {
        TableModel table = getTableForUser(tableId, email);
        UUID establishmentId = table.getEstablishment().getId();

        requireOwner(email, establishmentId);

        CardModel card = cardRepository
                .findByIdAndEstablishmentId(cardId, establishmentId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Cartão não encontrado neste estabelecimento"
                ));

        if (!Boolean.TRUE.equals(card.getActive())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Não é possível associar um cartão inativo"
            );
        }

        if (tableRepository.existsByCard_IdAndIdNot(cardId, tableId)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Este cartão já está associado a outra mesa"
            );
        }

        table.setCard(card);
        return tableRepository.save(table);
    }

    @Transactional
    public void delete(UUID tableId, String email) {
        TableModel table = getTableForUser(tableId, email);
        UUID establishmentId = table.getEstablishment().getId();

        requireOwner(email, establishmentId);

        table.setActive(false);

        tableRepository.save(table);
    }

    @Transactional
    public TableModel findById(UUID tableId, String email) {
        return getTableForUser(tableId, email);
    }

    @Transactional
    public TableModel findByCardId(UUID cardId, String email) {
        TableModel table = tableRepository.findByCard_Id(cardId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Mesa não encontrada para este cartão"
                ));

        requireMembership(
                email,
                table.getEstablishment().getId()
        );

        return table;
    }

    @Transactional
    public List<TableModel> findByEstablishmentId(
            UUID establishmentId,
            String email
    ) {
        requireMembership(email, establishmentId);

        return tableRepository.findByEstablishmentId(establishmentId);
    }
}