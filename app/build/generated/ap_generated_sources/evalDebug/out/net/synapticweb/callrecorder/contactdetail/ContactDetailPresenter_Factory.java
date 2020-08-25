// Generated by Dagger (https://dagger.dev).
package net.synapticweb.callrecorder.contactdetail;

import dagger.internal.Factory;
import javax.inject.Provider;
import net.synapticweb.callrecorder.data.Repository;

@SuppressWarnings({
    "unchecked",
    "rawtypes"
})
public final class ContactDetailPresenter_Factory implements Factory<ContactDetailPresenter> {
  private final Provider<ContactDetailContract.View> viewProvider;

  private final Provider<Repository> repositoryProvider;

  public ContactDetailPresenter_Factory(Provider<ContactDetailContract.View> viewProvider,
      Provider<Repository> repositoryProvider) {
    this.viewProvider = viewProvider;
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public ContactDetailPresenter get() {
    return newInstance(viewProvider.get(), repositoryProvider.get());
  }

  public static ContactDetailPresenter_Factory create(
      Provider<ContactDetailContract.View> viewProvider, Provider<Repository> repositoryProvider) {
    return new ContactDetailPresenter_Factory(viewProvider, repositoryProvider);
  }

  public static ContactDetailPresenter newInstance(ContactDetailContract.View view,
      Repository repository) {
    return new ContactDetailPresenter(view, repository);
  }
}