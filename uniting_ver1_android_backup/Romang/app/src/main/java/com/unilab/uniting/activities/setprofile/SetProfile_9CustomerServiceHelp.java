package com.unilab.uniting.activities.setprofile;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.unilab.uniting.R;
import com.unilab.uniting.adapter.setprofile.SetProfileHelpAdapter;
import com.unilab.uniting.model.Help;
import com.unilab.uniting.utils.BasicActivity;

import java.util.ArrayList;

public class SetProfile_9CustomerServiceHelp extends BasicActivity {

    final static String TAG = "CUSTOMER_HELP_TAG";

    private LinearLayout mBack;
    private RecyclerView mHelpRecyclerView;

    private ArrayList<Help> mHelpList;
    private SetProfileHelpAdapter mHelpAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_profile_9_customer_service_help);

        mBack = findViewById(R.id.toolbar_back);
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //공지사항 리스트
        mHelpRecyclerView = findViewById(R.id.setprofile_customer_service_rv_notice);
        mHelpList = new ArrayList<>();

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mHelpRecyclerView.setLayoutManager(layoutManager);
        mHelpAdapter = new SetProfileHelpAdapter(this, mHelpList);
        mHelpRecyclerView.setAdapter(mHelpAdapter);

        mHelpList.clear();
        mHelpAdapter.clear();

        Help help0 = new Help("유니팅 서비스 이용 방법을 알려주세요.", "유니팅 서비스는 크게 '오늘의 소개' 탭과 '미팅/셀소'탭의 2가지로 나뉘어져 있습니다. '오늘의 소개'탭은 유니팅 측에서 추천해주는 회원이 소개됩니다. '미팅/셀소'탭에서는 본인이 직접 스스로를 어필하는 미팅 혹은 셀프소개팅을 올릴 수 있습니다.");
        Help help1 = new Help("오늘의 소개 이용방법을 설명해주세요.", "오늘의 소개는 매일 저녁 8시 2장씩 소개되며, 수시적으로 추가 소개되는 경우도 있습니다. 마음에 드는 회원이 소개되었을 경우 '괜찮아요'로 호감을 보내거가 '친구 신청' 보내 마음을 표현할 수 있습니다. \n\n 상대방이 나의 '친구 신청'을를 수락할 경우 채팅으로 연결됩니다. 원하는 사람이 바로 채팅창을 열고 채팅을 시작할 수 있습니다. ");
        Help help2 = new Help("미팅/셀소 탭 이용방법을 설명해주세요.", " 미팅/셀소 탭에서는 본인이 스스로를 어필하는 글을 작성할 수 있습니다. 단, 미팅글에 본인의 얼굴이 드러나는 사진을 사용할 수 없습니다. 본인이 주최한 미팅에 사람들이 지원하면, 주최자는 지원자의 프로필을 볼 수 있습니다. 마음에 드는 지원자가 있을 경우, 신청을 수락하면 지원자도 주최자의 프로필을 볼 수 있게 됩니다. \n\n 서로의 프로필을 확인한 후 마음에 들었다면 '연락처 교환'을 신청할 수 있게 됩니다. 이 때 연락처교환을 신청하여도 상대방이 수락한 경우에만 연락처가 교환됩니다. '미팅/셀소'탭은 본인이 원하여 직접 올린 글이라는 점에서 오늘의 소개와 달리 채팅 연결이 아닌 실제 연락처가 바로 교환되는 방식으로 연결되도록 하였습니다. \n\n 미팅 주최자는 항상 대학과 전공 중 하나의 항목을 공개해야 합니다. (본인이 공개 선택해 놓은 항목이 자동으로 공개됩니다.)");
        Help help3 = new Help("추천 탭 이용방법을 설명해주세요.", " 추천 탭에는 유니팅에서 활동중인 회원분들이 자체 알고리즘에 의하여 소개되고 있습니다. 상대방이 마음에 들 경우 '괜찮아요' 로 호감을 보내주세요. 상대방의 '나에게 호감'탭에 회원님의 카드가 전달됩니다. \n\n 또한 많은 선호도를 정해 주실 수록 유니팅의 알고리즘이 더 좋은 추천을 해드릴 수 있습니다.");
        Help help4 = new Help("커뮤니티 탭 이용 방법을 설명해주세요.", " 커뮤니티는 자유로운 주제로 대화할 수 있는 익명 게시판입니다. 전체 게시판은 유니팅 회원 모두가 사용할 수 있으며, 대학별 게시판은 대학을 인증한 회원님에 한해 이용할 수 있습니다. \n\n 모든 주제로 이야기 가능하지만, 특정인을 저격하는 글, 본인의 연락처를 남기는 글, 미풍양속에 어긋나는 글 등은 제재 대상이 됩니다.");
        Help help5 = new Help("매칭 탭 이용 방법을 설명해주세요.",  " 오늘의 소개 탭에서 연결된 회원님들이 리스팅되는 탭입니다. '오늘의 소개'로 연결된 회원님은 바로 채팅을 열 수 있으며, 최대 10분까지 통화도 할 수 있어요.");
        Help help6 = new Help("대학, 전공 옆의 노란 마크는 무엇인가요?", " 대학, 전공을 인증한 회원님에게 붙여드리는 마크입니다.");
        Help help7 = new Help("대학, 전공은 어디서 인증하나요?", " 설정 탭에서 '인증'버튼을 누르시면 대학과 전공을 인증하실 수 있습니다.");
        Help help8 = new Help("대학과 전공을 인증할 경우 모두 공개되나요?", "대학과 전공중 하나만 공개할 수 있습니다. 둘 중 어느 항목을 공개할지는 '인증'탭에서 본인이 언제든지 변경할 수 있습니다.");
        Help help9 = new Help("지인을 차단하고 싶어요.", " 설정 탭에서 페이스북 친구, 같은 학교, 연락처상의 지인들을 차단할 수 있습니다.");
        Help help10 = new Help("오류를 발견했어요.", " help@uniting.kr 으로 알려주시면 신속하게 반영하겠습니다.");
        Help help11 = new Help("결제했지만 다이아를 받지 못했어요.", " help@uniting.kr 으로 알려주시면 처리해드리겠습니다.");
        Help help12 = new Help("목록에 있던 상대가 사라졌어요.", " 상대방이 회원님을 차단할 경우 목록에서 사라지게 됩니다.");
        Help help13 = new Help("도움말에 없는 문제가 있어요", " help@uniting.kr 으로 알려주시면 처리해드리겠습니다.");


        mHelpList.add(help0);
        mHelpList.add(help1);
        mHelpList.add(help2);
        mHelpList.add(help3);
        mHelpList.add(help4);
        mHelpList.add(help5);
        mHelpList.add(help6);
        mHelpList.add(help7);
        mHelpList.add(help8);
        mHelpList.add(help9);
        mHelpList.add(help10);
        mHelpList.add(help11);
        mHelpList.add(help12);
        mHelpList.add(help13);

        mHelpAdapter.notifyDataSetChanged();
    }
}
