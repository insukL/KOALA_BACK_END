package in.koala.serviceImpl;

import in.koala.domain.Scrap;
import in.koala.mapper.ScrapMapper;
import in.koala.service.ScrapService;
import in.koala.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Service
public class ScrapServiceImpl implements ScrapService {
    @Resource
    ScrapMapper scrapMapper;

    @Override
    public void Scrap(Scrap scrap) throws Exception {
        // 권한 체크
        //Long user_id = userService.로그인.getId();
        // 중복 스크랩 체크
        // 게시글 체크

        //scrapMapper.scrapBoard(user_id, board_id);
    }

    @Override
    public void deleteScrap(Long board_id) throws Exception {
        // 권한 체크
        // 게시글 유무 확인

        scrapMapper.deleteScrap(board_id);
    }

    @Override
    public void deleteAllScrap(Long user_id) throws Exception {
        // 권한 체크
        // 게시글 유무 확인

        scrapMapper.deleteAllScrap(user_id);
    }
    /*
    @Override
    public List<Board> getScrap(Long user_id) throws Exception {
        return null;
    }

     */
}
