package in.koala.serviceImpl;

import in.koala.domain.Memo;
import in.koala.mapper.MemoMapper;
import in.koala.service.MemoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Service
public class MemoServiceImpl implements MemoService {

    @Resource
    MemoMapper memoMapper;

    @Override
    public void addMemo(Memo memo) throws Exception {
        // 권한 체크
        // 스크랩 존재 여부, 메모 존재 여부

        memoMapper.addMemo(memo);
    }

    @Override
    public void deleteMemo(Memo memo) throws Exception{
        // 권한 체크
        // 스크랩 존재 여부, 메모 존재 여부

        memoMapper.deleteMemo(memo);
    }

    @Override
    public void updateMemo(Memo memo) throws Exception{
        // 권한 체크
        // 스크랩 존재 여부, 메모 존재 여부

        memoMapper.updateMemo(memo);
    }

    @Override
    public String getMemo(Long user_scrap_id) throws Exception{
        // 권한 체크
        // 스크랩 존재 여부, 메모 존재 여부

        return memoMapper.getMemo(user_scrap_id);
    }
}
