// Generated by Dagger (https://dagger.dev).
package net.synapticweb.callrecorder.contactdetail;

import dagger.MembersInjector;
import dagger.internal.InjectedFieldSignature;
import javax.inject.Provider;

@SuppressWarnings({
    "unchecked",
    "rawtypes"
})
public final class ContactDetailFragment_MembersInjector implements MembersInjector<ContactDetailFragment> {
  private final Provider<ContactDetailContract.Presenter> presenterProvider;

  public ContactDetailFragment_MembersInjector(
      Provider<ContactDetailContract.Presenter> presenterProvider) {
    this.presenterProvider = presenterProvider;
  }

  public static MembersInjector<ContactDetailFragment> create(
      Provider<ContactDetailContract.Presenter> presenterProvider) {
    return new ContactDetailFragment_MembersInjector(presenterProvider);}

  @Override
  public void injectMembers(ContactDetailFragment instance) {
    injectPresenter(instance, presenterProvider.get());
  }

  @InjectedFieldSignature("net.synapticweb.callrecorder.contactdetail.ContactDetailFragment.presenter")
  public static void injectPresenter(ContactDetailFragment instance,
      ContactDetailContract.Presenter presenter) {
    instance.presenter = presenter;
  }
}