package com.studyclock.timer.service;

import com.studyclock.timer.entity.Subject;
import com.studyclock.timer.repository.SubjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class SubjectService {

    private final SubjectRepository repository;

    public SubjectService(SubjectRepository repository) {
        this.repository = repository;
    }

    public List<Subject> list() {
        return repository.findAllByOrderBySortOrderAsc();
    }

    public Subject create(String name) {
        Subject subject = new Subject();
        subject.setName(name);
        return repository.save(subject);
    }

    public Subject update(Long id, String name) {
        Subject subject = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subject not found: " + id));
        subject.setName(name);
        return repository.save(subject);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}
