package com.example.caculosTrabalista.mapper;

import com.example.caculosTrabalista.DTO.EmpregadoRequest;
import com.example.caculosTrabalista.entity.Empregado;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CaculosMapper {

    Empregado toEntity(EmpregadoRequest Request);

}
