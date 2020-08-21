// Generated by Dagger (https://dagger.dev).
package net.synapticweb.callrecorder.contactslist;

import dagger.MembersInjector;
import dagger.internal.InjectedFieldSignature;
import javax.inject.Provider;

@SuppressWarnings({
    "unchecked",
    "rawtypes"
})
public final class ContactsListFragment_MembersInjector implements MembersInjector<ContactsListFragment> {
  private final Provider<ContactsListContract.Presenter> presenterProvider;

  public ContactsListFragment_MembersInjector(
      Provider<ContactsListContract.Presenter> presenterProvider) {
    this.presenterProvider = presenterProvider;
  }

  public static MembersInjector<ContactsListFragment> create(
      Provider<ContactsListContract.Presenter> presenterProvider) {
    return new ContactsListFragment_MembersInjector(presenterProvider);}

  @Override
  public void injectMembers(ContactsListFragment instance) {
    injectPresenter(instance, presenterProvider.get());
  }

  @InjectedFieldSignature("net.synapticweb.callrecorder.contactslist.ContactsListFragment.presenter")
  public static void injectPresenter(ContactsListFragment instance,
      ContactsListContract.Presenter presenter) {
    instance.presenter = presenter;
  }
}
