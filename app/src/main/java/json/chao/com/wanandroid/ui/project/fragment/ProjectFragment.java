package json.chao.com.wanandroid.ui.project.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.flyco.tablayout.SlidingTabLayout;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import json.chao.com.wanandroid.component.RxBus;
import json.chao.com.wanandroid.core.DataManager;
import json.chao.com.wanandroid.core.bean.project.ProjectClassifyData;
import json.chao.com.wanandroid.core.bean.project.ProjectClassifyResponse;
import json.chao.com.wanandroid.R;
import json.chao.com.wanandroid.app.Constants;
import json.chao.com.wanandroid.base.fragment.BaseFragment;
import json.chao.com.wanandroid.contract.project.ProjectContract;
import json.chao.com.wanandroid.core.event.DismissErrorView;
import json.chao.com.wanandroid.core.event.JumpToTheTopEvent;
import json.chao.com.wanandroid.core.event.ShowErrorView;
import json.chao.com.wanandroid.presenter.project.ProjectPresenter;
import json.chao.com.wanandroid.utils.CommonUtils;

/**
 * @author quchao
 * @date 2018/2/11
 */

public class ProjectFragment extends BaseFragment<ProjectPresenter> implements ProjectContract.View {

    @BindView(R.id.project_tab_layout)
    SlidingTabLayout mTabLayout;
    @BindView(R.id.project_viewpager)
    ViewPager mViewPager;

    @Inject
    DataManager mDataManager;

    private List<ProjectClassifyData> mData;
    private ArrayList<BaseFragment> mFragments;
    private int currentPage;

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDataManager.setProjectCurrentPage(currentPage);
    }

    public static ProjectFragment getInstance(String param1, String param2) {
        ProjectFragment fragment = new ProjectFragment();
        Bundle args = new Bundle();
        args.putString(Constants.ARG_PARAM1, param1);
        args.putString(Constants.ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_project;
    }

    @Override
    protected void initEventAndData() {
        mFragments = new ArrayList<>();
        mPresenter.getProjectClassifyData();
        currentPage = mDataManager.getProjectCurrentPage();
    }

    @Override
    protected void initInject() {
        getFragmentComponent().inject(this);
    }

    @Override
    public void showProjectClassifyData(ProjectClassifyResponse projectClassifyResponse) {
        if (projectClassifyResponse == null || projectClassifyResponse.getData() == null) {
            showProjectClassifyDataFail();
            return;
        }
        RxBus.getDefault().post(new DismissErrorView());
        mTabLayout.setVisibility(View.VISIBLE);
        mViewPager.setVisibility(View.VISIBLE);
        mData = projectClassifyResponse.getData();
        for (ProjectClassifyData data : mData) {
            ProjectListFragment projectListFragment = ProjectListFragment.getInstance(data.getId(), null);
            mFragments.add(projectListFragment);
        }
        mViewPager.setAdapter(new FragmentPagerAdapter(getChildFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return mFragments.get(position);
            }

            @Override
            public int getCount() {
                return mData == null? 0 : mData.size();
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return mData.get(position).getName();
            }
        });

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentPage = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mTabLayout.setViewPager(mViewPager);
        mViewPager.setCurrentItem(Constants.TAB_ONE);
    }

    @Override
    public void showProjectClassifyDataFail() {
        CommonUtils.showMessage(_mActivity, getString(R.string.failed_to_obtain_project_classify_data));
    }

    @Override
    public void showError() {
        mTabLayout.setVisibility(View.GONE);
        mViewPager.setVisibility(View.GONE);
        RxBus.getDefault().post(new ShowErrorView());
    }

    public void reLoad() {
        if (mPresenter != null) {
            mPresenter.getProjectClassifyData();
        }
    }

    public void jumpToTheTop() {
        if (mFragments != null) {
            RxBus.getDefault().post(new JumpToTheTopEvent());
        }
    }

}

