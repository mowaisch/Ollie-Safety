package com.olliesafety2.simpleapplocker.admin_side;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;



import java.util.ArrayList;
import java.util.List;

public class PageViewerAdaptar extends FragmentPagerAdapter {

    private List<Fragment> MyFragment = new ArrayList<>();
    private List<String> MyPageTittle = new ArrayList<>();

    public PageViewerAdaptar(FragmentManager fm) {
        super(fm);
    }
    @Override
    public Fragment getItem(int position) {
        return MyFragment.get(position);
    }
    @Override
    public int getCount() {
        return MyPageTittle.size();
    }
    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return MyPageTittle.get(position);
    }
    public void AddFragmentPage(Fragment Frag, String Title){
        MyFragment.add(Frag);
        MyPageTittle.add(Title);
    }


}
