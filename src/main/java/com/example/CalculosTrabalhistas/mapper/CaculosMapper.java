package com.example.CalculosTrabalhistas.mapper;

import com.example.CalculosTrabalhistas.DTO.EmpregadoRequest;
import com.example.CalculosTrabalhistas.entity.Empregado;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CaculosMapper {

    Empregado toEntity(EmpregadoRequest requisicao);

}
