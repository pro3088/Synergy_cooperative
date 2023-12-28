package com.synergy.synergy_cooperative.docs;

import com.synergy.synergy_cooperative.util.NotFoundException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class DocsService {

    private final DocsRepository docsRepository;

    public DocsService(final DocsRepository docsRepository) {
        this.docsRepository = docsRepository;
    }

    public List<DocsDTO> findAll() {
        final List<Docs> docses = docsRepository.findAll(Sort.by("id"));
        return docses.stream()
                .map(docs -> mapToDTO(docs, new DocsDTO()))
                .collect(Collectors.toList());
    }

    public DocsDTO get(final String id) {
        return docsRepository.findById(id)
                .map(docs -> mapToDTO(docs, new DocsDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public String create(final DocsDTO docsDTO) {
        final Docs docs = new Docs();
        mapToEntity(docsDTO, docs);
        String uuid = UUID.randomUUID().toString();
        docs.setId(uuid);
        return docsRepository.save(docs).getId();
    }

    public void update(final String id, final DocsDTO docsDTO) {
        final Docs docs = docsRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(docsDTO, docs);
        docsRepository.save(docs);
    }

    public void delete(final String id) {
        docsRepository.deleteById(id);
    }

    private DocsDTO mapToDTO(final Docs docs, final DocsDTO docsDTO) {
        docsDTO.setId(docs.getId());
        docsDTO.setImage(docs.getImage());
        docsDTO.setText(docs.getText());
        return docsDTO;
    }

    private Docs mapToEntity(final DocsDTO docsDTO, final Docs docs) {
        docs.setImage(docsDTO.getImage());
        docs.setText(docsDTO.getText());
        return docs;
    }

}
