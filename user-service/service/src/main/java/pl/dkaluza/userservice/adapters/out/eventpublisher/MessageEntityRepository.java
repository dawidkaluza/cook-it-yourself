package pl.dkaluza.userservice.adapters.out.eventpublisher;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.ListPagingAndSortingRepository;

interface MessageEntityRepository extends ListPagingAndSortingRepository<MessageEntity, Long>, CrudRepository<MessageEntity, Long> {
}
