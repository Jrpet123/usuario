package com.josepetrucio.usuario.infrastructure.repository;

import com.josepetrucio.usuario.infrastructure.entity.Telefone;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TelefoneRepository extends JpaRepository<Telefone, Long> {
}
